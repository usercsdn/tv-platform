package com.taozi.tv.dao;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface BaseDao<E> {

	@InsertProvider(method = "create", type = BaseProvider.class)
	public void create(E e);

	@UpdateProvider(method = "update", type = BaseProvider.class)
	public void update(E e);

	@DeleteProvider(method = "delete", type = BaseProvider.class)
	public void delete(E e);

	@SelectProvider(method = "getOne", type = BaseProvider.class)
	public E getOne(E e);

	@SelectProvider(method = "count", type = BaseProvider.class)
	public long count(E e);

	@SelectProvider(method = "queryByPage", type = BaseProvider.class)
	public List<E> queryByPage(E e);

	@SelectProvider(method = "query", type = BaseProvider.class)
	public List<E> query(E e);

	public List<E> selfSql(E bean);
}
