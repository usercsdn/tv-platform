package com.taozi.tv.elastic;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.repository.CrudRepository;

import com.taozi.tv.dao.bean.VideoResource;

public interface VideoResourceESDao extends CrudRepository<VideoResource, Long> {
	@Highlight(fields = {
			@HighlightField(name = "name") }, parameters = @HighlightParameters(preTags = "<span class=\"mark\">", postTags = "</span>"))
	public SearchPage<VideoResource> findByNameLike(String name, Pageable page);

	public List<VideoResource> findByName(String name);
}
