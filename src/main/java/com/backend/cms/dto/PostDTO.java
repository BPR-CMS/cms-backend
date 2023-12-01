package com.backend.cms.dto;


import com.backend.cms.model.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class PostDTO {


    private String id;

    private String collectionId;

    private String userId;

    private Map<String, Object> attributes;

    public PostDTO(Post post) {
        this.id = post.getPostId();
        this.collectionId = post.getCollectionId();
        this.userId = post.getUserId();
        this.attributes = post.getAttributes();
    }

    public static PostDTO fromPost(Post post) {
        return new PostDTO(post);
    }
}
