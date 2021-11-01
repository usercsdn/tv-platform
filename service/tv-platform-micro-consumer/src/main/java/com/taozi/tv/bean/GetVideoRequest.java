package com.taozi.tv.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetVideoRequest implements Serializable {
	private static final long serialVersionUID = 3998325483424105633L;
	private Long id;
	private Integer offset;
}
