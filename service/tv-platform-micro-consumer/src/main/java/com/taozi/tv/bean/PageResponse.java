package com.taozi.tv.bean;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xulijie
 * @date 2021/06/30
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> implements Serializable {
	private static final long serialVersionUID = -1629599186430076732L;
	private Integer limit;
	private Integer total;
	private List<T> datas;
}
