package com.web.test.dao;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.util.ObjectUtils;

import com.taozi.tv.Bootstrap;
import com.taozi.tv.dao.VideoResourceDao;
import com.taozi.tv.dao.bean.VideoResource;
import com.taozi.tv.elastic.VideoResourceESDao;

import lombok.SneakyThrows;

@SpringBootApplication(scanBasePackages = { "com.web.tv" })
@MapperScan(basePackages = { "com.taozi.tv.dao" })
public class ElasticSearchDaoTest {
	static VideoResourceESDao service;
	static VideoResourceDao videoResourceDao;
	static ElasticsearchOperations bean;

	public static void main(String[] args) {
		ElasticSearchDaoTest api = new ElasticSearchDaoTest();
		ConfigurableApplicationContext run = SpringApplication.run(Bootstrap.class, args);
		api.service = run.getBean(VideoResourceESDao.class);
		api.videoResourceDao = run.getBean(VideoResourceDao.class);
		api.bean = run.getBean(ElasticsearchOperations.class);
		api.start();
	}

	private void start() {
		// deleteAll();
		// create();
		// query();
		// printAll();
		init();
	}

	private void init() {
		VideoResource e = VideoResource.builder().build();
		List<VideoResource> query = videoResourceDao.query(e);
		query.forEach((el) -> {
			service.save(el);
		});
	}

	private void create() {
		service.save(VideoResource.builder().id(1L).name("打压").createTime(new Date()).build());
		// IntStream.range(1, 4).forEach(i -> service
		// .save(Book.builder().id(i).bookName("打压" +
		// i).author("hello").createTime(new Date()).build()));
	}

	private void deleteAll() {
		Iterable<VideoResource> findAll = service.findAll();
		findAll.forEach((el) -> {
			service.delete(el);
		});
	}

	@SneakyThrows
	private void query() {
		// Date parseDate = DateUtils.parseDate("2021-06-29 15:13:00", new
		// String[] { "yyyy-MM-dd HH:mm:ss" });
		// Date end = DateUtils.parseDate("2021-06-29 16:13:00", new String[] {
		// "yyyy-MM-dd HH:mm:ss" });
		// Date parseDate = DateUtils.parseDate("2021-06-29", new String[] {
		// "yyyy-MM-dd" });
		// Date end = DateUtils.parseDate("2021-06-30", new String[] {
		// "yyyy-MM-dd" });
		// Criteria criteria = new Criteria("name").matches("打");
		// Query query = new CriteriaQuery(criteria);
		// SearchHits<VideoResource> search = bean.search(query,
		// VideoResource.class);
		// search.forEach((el) -> {
		// System.out.println("query:" + el);
		// });

		IntStream.range(0, 4).forEach(i -> {
			SearchPage<VideoResource> findByBookName = service.findByNameLike("用钢丝球", PageRequest.of(0, 100));
			System.out.println(findByBookName.getContent());
			List<SearchHit<VideoResource>> content = findByBookName.getContent();
			content.forEach((el) -> {
				List<String> highlightField = el.getHighlightField("name");
				System.out.println(highlightField.size());
				System.out.println(el);

				StringBuilder sb = new StringBuilder();
				if (!ObjectUtils.isEmpty(highlightField)) {
					highlightField.forEach(sb::append);
					el.getContent().setName(sb.toString());
				}
			});

			findByBookName.forEach((el) -> {
				System.out.println(el.getContent());
			});
			List<VideoResource> findByBookName2 = service.findByName("用钢丝球");
			System.out.println(findByBookName2.size());
		});

	}

	private void printAll() {
		Iterable<VideoResource> findAll = service.findAll();
		findAll.forEach((el) -> {
			System.out.println("printAll:" + el);
		});
		// System.out.println(findById);
		// service.update(e);
	}

}
