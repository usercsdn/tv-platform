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
public class GetRcommandRequest implements Serializable {
	private static final long serialVersionUID = -6962760395385039749L;
	private Long tagId;
	private String tagType;
	private int beginNum;
	private int endNum;
}
