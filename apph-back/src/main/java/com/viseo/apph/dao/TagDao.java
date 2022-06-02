package com.viseo.apph.dao;

import com.viseo.apph.domain.Tag;
import com.viseo.apph.dto.TagResponse;
import javafx.util.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TagDao {
    @PersistenceContext
    EntityManager em;

    public List<Tag> getTagsByUser(long userId) {
        List<Tag> tags = em.createQuery("SELECT t FROM Tag t WHERE t.user.id=:userId", Tag.class)
                .setParameter("userId", userId).getResultList();
        List<Tag> filteredTag = new ArrayList<>();
        for (Tag tag : tags) {
            filteredTag.add((Tag) new Tag().setName(tag.getName()).setId(tag.getId()));
        }
        return filteredTag;
    }

    public Tag createTag(Tag tag) {
        em.persist(tag);
        return tag;
    }

    public List<Pair<String, Integer>> getTagsCountByUser(long userId) {
        List tags = em.createQuery("SELECT t.name,COUNT(t) FROM Photo p JOIN p.tags AS t WHERE t.user.id=:userId GROUP BY t.name")
                .setParameter("userId", userId).getResultList();
        List<Pair<String, Integer>> filteredTag = new ArrayList<>();
        for (Object[] tag : (List<Object[]>) tags) {
            filteredTag.add(new Pair<String, Integer>((String) tag[0], (int) ((long) tag[1])));
        }
        return filteredTag;
    }
}
