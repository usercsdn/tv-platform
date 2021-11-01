package com.taozi.tv.dao;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.dao.bean.Table;
import com.taozi.tv.dao.bean.TableColumn;
import com.taozi.tv.dao.bean.VideoResource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BaseProvider {
	private static final char UNDERLINE = '_';

	@SneakyThrows
	public String create(Object params) {
		SQL sql = new SQL() {
			{
				INSERT_INTO(getTable(params));
				Class<? extends Object> clz = params.getClass();
				Field[] declaredFields = clz.getDeclaredFields();
				for (Field el : declaredFields) {
					el.setAccessible(true);
					Object object = el.get(params);
					if (!ObjectUtils.isEmpty(object)) {
						TableColumn annotation = el.getAnnotation(TableColumn.class);
						if (!ObjectUtils.isEmpty(annotation)) {
							String annotationValue = annotation.value();
							if (ObjectUtils.isEmpty(annotationValue)) {
								annotationValue = camelToUnderline(el.getName());
							}
							if (object instanceof Date) {
								String value = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(object);
								VALUES(annotationValue, "'" + value + "'");
							} else {
								VALUES(annotationValue, "'" + Objects.toString(object) + "'");
							}

						}
					}
				}
			}
		};
		log.info("query is sql:{}", sql);
		return sql.toString();
	}

	@SneakyThrows
	public String update(Object params) {
		SQL sql = new SQL() {
			{
				UPDATE(getTable(params));
				SET(getConditionsOfUpdate(params, " , "));
				WHERE(getConditionsId(params));
			}
		};
		log.info("query is sql:{}", sql);
		return sql.toString();
	}

	@SneakyThrows
	public String count(Object params) {
		SQL sql = new SQL() {
			{
				SELECT("count(1)");
				FROM(getTable(params));
				WHERE(getConditions(params, " and "));
			}
		};
		log.info("query is sql:{}", sql);
		return sql.toString();
	}

	public String delete(Object params) {
		SQL sql = new SQL() {
			{
				DELETE_FROM(getTable(params));
				WHERE(getConditionsId(params));
			}
		};
		log.info("query is sql:{}", sql);
		return sql.toString();
	}

	public String query(Object params) {
		SQL sql = new SQL() {
			{
				SELECT("*");
				FROM(getTable(params));
				WHERE(getConditions(params, " and "));
			}
		};
		log.info("query is sql:{}", sql);
		return sql.toString();
	}

	public String getOne(Object params) {
		SQL sql = new SQL() {
			{
				SELECT("*");
				FROM(getTable(params));
				WHERE(getConditions(params, " and "));
				LIMIT("1");
			}
		};
		log.info("query is sql:{}", sql);
		return sql.toString();
	}

	public String queryByPage(Object params) {
		SQL sql = new SQL();
		sql.SELECT("*");
		sql.FROM(getTable(params));
		sql.WHERE(getConditions(params, " and "));
		StringBuilder value = new StringBuilder(sql.toString());

		String orderType = getConditionsSingle(params, "orderType");
		if (!ObjectUtils.isEmpty(orderType)) {
			value.append(" order by ").append(orderType);
			String desc = Objects.toString(getConditionsSingle(params, "descType"), "DESC");
			value.append(" ").append(desc).append(" ");
		}
		String limit = getConditionsLimit(params);
		if (!ObjectUtils.isEmpty(limit)) {
			value.append(" limit ").append(limit);
		}
		log.info("query is sql:{}", value);
		return value.toString();
	}

	public String getTable(Object params) {
		Class<? extends Object> clz = params.getClass();
		Table annotation = clz.getAnnotation(Table.class);
		Assert.isTrue(!ObjectUtils.isEmpty(annotation), "BaseProvider not TableAnnotation of params:" + params);
		return annotation.value();
	}

	@SneakyThrows
	public static void main(String[] args) {
		BaseProvider api = new BaseProvider();
		String camelToUnderline = camelToUnderline("aBcD");
		System.out.println(camelToUnderline);
		String conditions = api
				.queryByPage(VideoResource.builder().id(12L).name("abc1").activeStatus(0).limit(2).offset(10).build());
		// System.out.println(conditions);
	}

	@SneakyThrows
	public String getConditions(Object params, String split) {
		Class<? extends Object> clz = params.getClass();
		StringBuilder sb = new StringBuilder();
		sb.append("1=1").append(split);
		Field[] declaredFields = clz.getDeclaredFields();
		for (Field el : declaredFields) {
			el.setAccessible(true);
			Object object = el.get(params);
			if (!ObjectUtils.isEmpty(object)) {
				TableColumn annotation = el.getAnnotation(TableColumn.class);
				if (!ObjectUtils.isEmpty(annotation)) {
					String annotationValue = annotation.value();
					if (ObjectUtils.isEmpty(annotationValue)) {
						annotationValue = camelToUnderline(el.getName());
					}
					if (object instanceof Date) {
						String value = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(object);
						sb.append(annotationValue).append("=").append("'").append(value).append("'").append(split);
					} else {
						sb.append(annotationValue).append("=").append("'").append(object).append("'").append(split);
					}
				}
			}
		}
		if (sb.length() > 0) {
			sb = sb.delete(sb.lastIndexOf(split), sb.length());
		}
		return sb.toString();
	}

	@SneakyThrows
	public String getConditionsOfUpdate(Object params, String split) {
		Class<? extends Object> clz = params.getClass();
		StringBuilder sb = new StringBuilder();
		Field[] declaredFields = clz.getDeclaredFields();
		for (Field el : declaredFields) {
			el.setAccessible(true);
			Object object = el.get(params);
			if (!ObjectUtils.isEmpty(object)) {
				TableColumn annotation = el.getAnnotation(TableColumn.class);
				if (!ObjectUtils.isEmpty(annotation)) {
					String annotationValue = annotation.value();
					if (ObjectUtils.isEmpty(annotationValue)) {
						annotationValue = camelToUnderline(el.getName());
					}
					if (el.getName().equalsIgnoreCase("id")) {
						continue;
					}
					if (object instanceof Date) {
						String value = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(object);
						sb.append(annotationValue).append("=").append("'").append(value).append("'").append(split);
					} else {
						sb.append(annotationValue).append("=").append("'").append(object).append("'").append(split);
					}
				}
			}
		}
		if (sb.length() > 0) {
			sb = sb.delete(sb.lastIndexOf(split), sb.length());
		}
		return sb.toString();
	}

	@SneakyThrows
	public String getConditionsId(Object params) {
		Class<? extends Object> clz = params.getClass();
		StringBuilder sb = new StringBuilder();
		Field[] declaredFields = clz.getDeclaredFields();
		for (Field el : declaredFields) {
			el.setAccessible(true);
			if (el.getName().equalsIgnoreCase("id")) {
				Object object = el.get(params);
				if (!ObjectUtils.isEmpty(object)) {
					TableColumn annotation = el.getAnnotation(TableColumn.class);
					if (!ObjectUtils.isEmpty(annotation)) {

						String annotationValue = annotation.value();
						if (ObjectUtils.isEmpty(annotationValue)) {
							annotationValue = camelToUnderline(el.getName());
						}
						sb.append(annotationValue).append("=").append(object);

					}
				}
				break;
			}
		}
		return sb.toString();
	}

	@SneakyThrows
	public String getConditionsLimit(Object params) {
		Class<? extends Object> clz = params.getClass();
		StringBuilder sb = new StringBuilder();
		Field[] declaredFields = clz.getDeclaredFields();
		String limit = "";
		String offset = "";
		for (Field el : declaredFields) {
			el.setAccessible(true);
			if (el.getName().equalsIgnoreCase("limit")) {
				limit = Objects.toString(el.get(params), "0");
			}
			if (el.getName().equalsIgnoreCase("offset")) {
				offset = Objects.toString(el.get(params), "0");
			}
		}
		if (!ObjectUtils.isEmpty(limit) && !ObjectUtils.isEmpty(offset)) {
			if (NumberUtils.toInt(limit) <= 0) {
				limit = "1";
			}
			int begin = (NumberUtils.toInt(limit) - 1) * NumberUtils.toInt(offset);
			begin = begin > 0 ? begin : 0;
			sb.append(begin).append(",").append(NumberUtils.toInt(offset));
		}
		return sb.toString();
	}

	@SneakyThrows
	public String getConditionsSingle(Object params, String key) {
		Class<? extends Object> clz = params.getClass();
		Field[] declaredFields = clz.getDeclaredFields();
		for (Field el : declaredFields) {
			el.setAccessible(true);
			if (el.getName().equalsIgnoreCase(key)) {
				return Objects.toString(el.get(params), "0");
			}
		}
		return "";
	}

	public static String camelToUnderline(String param) {
		if (StringUtils.isEmpty(param)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int len = param.length();
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append(UNDERLINE);
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
