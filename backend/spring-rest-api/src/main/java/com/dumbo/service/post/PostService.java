package com.dumbo.service.post;

import org.springframework.stereotype.Service;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.Post;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;

@Service
public interface PostService 
{
    public Post createPost (User user, PostDTO postDto) throws DatabaseWriteException;
    public CursorResult<ArticleDTO> getArticleFeed(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws DatabaseReadException;

    public void deletePost(User user, String postId);

    public boolean isPostExist(String postid) throws DatabaseReadException;
}
