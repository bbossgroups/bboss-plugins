package org.frameworkset.elasticsearch.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合查询，并且返回结果集
 * @param <Hits>
 * @param <Aggs>
 */
public class ESHitAggDatas<Hits,Aggs>  implements Serializable {
	private long totalSize;
	private List<Aggs> aggDatas;
	/**
	 * 当前获取的记录集合
	 */
	private List<Hits> hitDatas;
}
