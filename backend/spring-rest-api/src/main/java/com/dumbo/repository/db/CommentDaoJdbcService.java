package com.dumbo.repository.db;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import com.dumbo.domain.dto.CommentDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.entity.Comment;
import com.dumbo.domain.entity.User;
import com.dumbo.repository.dao.CommentDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class CommentDaoJdbcService implements CommentDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean createComment(User user, String postId, String content)
    {
        String sql = "INSERT INTO comments (post_id, author_id, author_nickname, content, reply_count) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, postId);
            ps.setString(2, user.getId());
            ps.setString(3, user.getNickname());
            ps.setString(4, content);
            ps.setInt(5, 0);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws SQLException
    {
        String sql = "SELECT id, author_id, author_nickname, content, created_at, updated_at, reply_count FROM comments WHERE (post_id = ? AND reply_to IS NULL)";
        boolean hasCursor = createdAtCursor != null && commentIdCursor != null;

        // 커서 처리
        if (hasCursor) 
        {
            if (reverse) sql += " AND (created_at > ? OR (created_at = ? AND id > ?)) ";
            else sql += " AND (created_at < ? OR (created_at = ? AND id < ?)) ";
        }

        // 정렬 처리 : 작성 시각이 겹치면 userId의 오름차순 정렬
        sql += " ORDER BY created_at " + (reverse ? "ASC" : "DESC") + ", id ASC LIMIT ?";

        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql)) 
        {
            int idx = 1;
            ps.setString(idx++, postId);

            if (hasCursor) 
            {
                Timestamp ts = new Timestamp(createdAtCursor * 1000); // 초 -> 밀리초 변환
                ps.setTimestamp(idx++, ts);
                ps.setTimestamp(idx++, ts);
                ps.setString(idx++, commentIdCursor);
            }

            ps.setInt(idx, limit + 1); // hasmore 추출 위해

            try (ResultSet rs = ps.executeQuery()) 
            {
                List<CommentDTO> comments = new ArrayList<>();

                while (rs.next()) 
                {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(rs.getString("id"));
                    dto.setAuthorId(rs.getString("author_id"));
                    dto.setAuthorNickname(rs.getString("author_nickname"));
                    dto.setContent(rs.getString("content"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    dto.setCreatedAt(createdAt.getTime()/1000); // 밀리초 -> 초
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    dto.setUpdatedAt(updatedAt != null ? updatedAt.getTime()/1000 : null); // 밀리초 -> 초
                    dto.setReplyCount(rs.getInt("reply_count"));
                    comments.add(dto);
                }

                boolean hasMore = comments.size() > limit;
                if (hasMore) comments.remove(comments.size() - 1); // hasmore 판별 후 제거

                Long nextCreatedAt = null;
                String nextCommentId = null;
                if (!comments.isEmpty()) 
                {
                    CommentDTO last = comments.get(comments.size() - 1);
                    nextCreatedAt = last.getCreatedAt();
                    nextCommentId = last.getId();
                }
                return new CursorResult<CommentDTO>(comments, nextCreatedAt, nextCommentId, hasMore);
            }
        }
    }

    // 댓글을 ID로 찾고, 있으면 해당 댓글 객체, 없으면 null 반환
    public Comment getCommentById(String commentId) throws SQLException
    {
        String sql = "SELECT id, post_id, reply_to, author_id, author_nickname, content, created_at, updated_at, reply_count FROM comments WHERE id = ?";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, commentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                Comment comment = new Comment();
                comment.setId(rs.getString("id"));
                comment.setPostId(rs.getString("post_id"));
                comment.setReplyTo(rs.getString("reply_to"));;
                comment.setAuthorId(rs.getString("author_nickname"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                comment.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
                comment.setReplyCount(rs.getInt("reply_count"));
                return comment;
            }
            else return null;
        }
    }

    public void createReply(User user, Comment comment, String postId, String content) throws SQLException
    {
        String sql = "INSERT INTO comments (post_id, reply_to, author_id, author_nickname, content) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, postId);
            ps.setString(2, comment.getId());
            ps.setString(3, user.getId());
            ps.setString(4, user.getNickname());
            ps.setString(5, content);
            ps.executeUpdate();
        }
    }

    public void incReplyCount(String commentId) throws SQLException
    {
        String sql = "UPDATE comments SET reply_count = reply_count + 1 WHERE (id = ? AND reply_count IS NOT NULL)"; // 안전성을 보장하기 위해 reply_count IS NOT NULL 추가
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, commentId);
            ps.executeUpdate();
        }
    }

    public CursorResult<CommentDTO> getReplysByComment(Comment comment, Long createdAtCursor, String replyIdCursor, int limit) throws SQLException 
    {
        boolean hasCursor = createdAtCursor != null && replyIdCursor != null;

        String sql = "SELECT id, post_id, reply_to, author_id, author_nickname, content, created_at, updated_at, reply_count FROM comments WHERE reply_to = ?";
        if (hasCursor) sql += " AND (created_at < ? OR (created_at = ? AND id > ?))";
        sql += " ORDER BY created_at DESC, id ASC LIMIT ?";

        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql)) 
        {
            int idx = 1;
            ps.setString(idx++, comment.getId());

            if (hasCursor) 
            {
                Timestamp ts = new Timestamp(createdAtCursor * 1000); // 초->밀리초 변환
                ps.setTimestamp(idx++, ts);
                ps.setTimestamp(idx++, ts);
                ps.setString(idx++, replyIdCursor);
            }

            ps.setInt(idx++, limit + 1); // hasMore 확인용

            try (ResultSet rs = ps.executeQuery()) 
            {
                List<CommentDTO> comments = new ArrayList<>();

                while (rs.next()) 
                {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(rs.getString("id"));
                    dto.setAuthorId(rs.getString("author_id"));
                    dto.setAuthorNickname(rs.getString("author_nickname"));
                    dto.setContent(rs.getString("content"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    dto.setCreatedAt(createdAt.getTime() / 1000); // 밀리초->초
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    dto.setUpdatedAt(updatedAt != null ? updatedAt.getTime() / 1000 : null);
                    dto.setReplyCount(rs.getInt("reply_count"));
                    comments.add(dto);
                }

                boolean hasMore = comments.size() > limit;
                if (hasMore) comments.remove(comments.size() - 1);

                Long nextCreatedAt = null;
                String nextCommentId = null;

                if (!comments.isEmpty()) 
                {
                    CommentDTO last = comments.get(comments.size() - 1);
                    nextCreatedAt = last.getCreatedAt();
                    nextCommentId = last.getId();
                }

                return new CursorResult<CommentDTO>(comments, nextCreatedAt, nextCommentId, hasMore);
            }
        }
    }

}
