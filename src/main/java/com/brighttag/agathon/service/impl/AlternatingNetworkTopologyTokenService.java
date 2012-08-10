package com.brighttag.agathon.service.impl;

import java.math.BigInteger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.TokenService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link TokenService} that assigns tokens for {@code NetworkTopologyStrategy}
 * deployments with alternating data centers. This requires the same number of nodes
 * in each data center. Additionally, the node IDs must start from "1" and increase
 * sequentially around the ring.
 *
 * Tokens are calculated for the nodes in each data center independently and
 * made unique by adding an offset for the data center to the token.
 *
 * The algorithm used to compute the initial token for an instance is
 * {@code i * (2**127 / N) + offset, for i = 0..N-1}, where {@code N} is the number
 * of data centers, {@code i} is the node position within the DC ring, and {@code offset}
 * is the offset of the data center as described above.
 *
 * For example, with three data centers (DC), 4 nodes per DC, and offsets of 0 for DC1,
 * 1 for DC2, and 2 for DC3, then you would have the following topology: <pre>{@code
 *     [DC1] node 1 = 0
 *     [DC2] node 2 = 1
 *     [DC3] node 3 = 2
 *     [DC1] node 4 = 42535295865117307932921825928971026432
 *     [DC2] node 5 = 42535295865117307932921825928971026433
 *     [DC3] node 6 = 42535295865117307932921825928971026434
 *     [DC1] node 7 = 85070591730234615865843651857942052864
 *     [DC2] node 8 = 85070591730234615865843651857942052865
 *     [DC3] node 9 = 85070591730234615865843651857942052866
 *     [DC1] node 10 = 127605887595351923798765477786913079296
 *     [DC2] node 11 = 127605887595351923798765477786913079297
 *     [DC3] node 12 = 127605887595351923798765477786913079298
 * }</pre>
 *
 * The equivalent python program to compute the example tokens is <pre>{@code
 *     def token(i, N, offset):
 *       return i * (2**127 / N) + offset
 *
 *     [token(node, 4, DC) for node in range(4) for DC in range(3)]
 * }</pre>
 *
 * @see <a href="http://wiki.apache.org/cassandra/Operations">Cassandra Operations</a>
 * @see <a href="http://www.datastax.com/docs/1.0/cluster_architecture/replication#networktopologystrategy">
 *      Network Topology Strategy</a>
 * @author codyaray
 * @since 6/5/12
 */
public class AlternatingNetworkTopologyTokenService implements TokenService {

  private static final BigInteger MAXIMUM_TOKEN = new BigInteger("2").pow(127);

  private final CassandraInstance coprocess;
  private final int numNodes;

  @Inject
  public AlternatingNetworkTopologyTokenService(@Coprocess CassandraInstance coprocess,
      @Named(ServiceModule.NODES_PER_DATACENTER_PROPERTY) int numNodes) {
    this.coprocess = coprocess;
    this.numNodes = numNodes;
  }

  @Override
  public BigInteger getToken() {
    return initialToken(numNodes,
        positionInDataCenter(coprocess.getId()),
        offsetForDataCenter(coprocess.getDataCenter()));
  }

  /**
   * Calculates the position of a node within an (identically-sized) data center,
   * given the absolute position of the node within the ring. Assumes that absolute
   * positioning begins with ID "1" and increases sequentially around the ring.
   *
   * @param id the absolute position of the node within the ring
   * @return the position of a node within an (identically-sized) data center
   */
  private int positionInDataCenter(int id) {
    checkArgument(id >= 1, "IDs must start with '1' and increase sequentially");
    return (id - 1) % numNodes;
  }

  /**
   * Calculates an offset for the given {@code dataCenter}. To support arbitrary
   * {@code dataCenter} names, the offset is computed from the {@code dataCenter}'s
   * {@link Object#hashCode hashCode}.
   *
   * @param dataCenter the data center name
   * @return (practically) unique offset for this data center
   */
  private static int offsetForDataCenter(String dataCenter) {
    // Avoid Math.abs(hashCode()) bug where Math.abs(Integer.MIN_VALUE) < 0
    // See http://findbugs.blogspot.com/2006/09/is-mathabs-broken.html
    return dataCenter.hashCode() & Integer.MAX_VALUE;
  }

  /**
   * Calculates the initial token for a given {@code position} within the data center
   * with the given {@code offset}, evenly spaced from the other {@code size-1} nodes.
   *
   * @param size number of nodes per data center
   * @param position node position within the data center ring
   * @param offset data center-specific offset
   * @return (MAXIMUM_TOKEN / size) * position + offset
   */
  @VisibleForTesting static BigInteger initialToken(int size, int position, int offset) {
    checkArgument(size > 0, "size must be > 0");
    checkArgument(offset >= 0, "offset must be >= 0");
    checkArgument(position >= 0 && position < size, "position must be >= 0 && < size");
    return MAXIMUM_TOKEN.divide(BigInteger.valueOf(size))
            .multiply(BigInteger.valueOf(position))
            .add(BigInteger.valueOf(offset));
  }

}
