package com.taozi.tv;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class CallResult<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5817032948682372914L;

	/**
	 * 是否响应成功
	 */
	private Boolean success;
	/**
	 * 响应状态码
	 */
	private Integer code;
	/**
	 * 响应数据
	 */
	private T data;
	/**
	 * 错误信息
	 */
	private String message;

	// 构造器开始
	/**
	 * 无参构造器(构造器私有，外部不可以直接创建)
	 */
	private CallResult() {
		this.code = 0;
		this.success = true;
	}

	/**
	 * 有参构造器
	 * 
	 * @param obj
	 */
	public CallResult(T obj) {
		this.code = 0;
		this.data = obj;
		this.success = true;
	}

	private CallResult(T obj, String message) {
		this.code = 0;
		this.data = obj;
		this.success = true;
		this.message = message;
	}

	/**
	 * 有参构造器
	 * 
	 * @param resultCode
	 */
	private CallResult(int resultCode, String message) {
		this.success = false;
		this.code = resultCode;
		this.message = message;
	}
	// 构造器结束

	/**
	 * 通用返回成功（没有返回结果）
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T> CallResult<T> success() {
		return new CallResult<T>();
	}

	/**
	 * 返回成功（有返回结果）
	 * 
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> CallResult<T> success(T data) {
		return new CallResult<T>(data, "success");
	}

	/**
	 * 通用返回失败
	 * 
	 * @param resultCode
	 * @param <T>
	 * @return
	 */
	public static <T> CallResult<T> failure(int resultCode, String message) {
		return new CallResult<T>(resultCode, message);
	}
}
