package cn.ictgu.serv.service;

import cn.ictgu.serv.mapper.CategoryItemMapper;
import cn.ictgu.serv.mapper.CategoryMapper;
import cn.ictgu.serv.model.CategoryItem;
import cn.ictgu.serv.model.enumclass.CategoryItemType;
import cn.ictgu.serv.service.CategoryItemService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryItem
 * Created by Silence on 2017/4/11.
 */
@Service
@Log4j2
public class CategoryItemServiceImpl implements CategoryItemService {

  @Autowired
  private CategoryItemMapper mapper;

  @Autowired
  private CategoryMapper categoryMapper;

  private Map<String, String> defaultImages;

  public CategoryItemServiceImpl() {
    defaultImages = new HashMap<>();
    defaultImages.put(CategoryItemType.VIDEO.name(), "/image/icon/icon-video.png");
    defaultImages.put(CategoryItemType.ARTICLE.name(), "/image/icon/icon-article.png");
    defaultImages.put(CategoryItemType.FILE.name(), "/image/icon/icon-file.png");
    defaultImages.put(CategoryItemType.CODE.name(), "/image/icon/icon-code.png");
    defaultImages.put(CategoryItemType.MUSIC.name(), "/image/icon/icon-music.png");
  }

  public List<CategoryItem> list(Long categoryId, Long userId) {
    List<CategoryItem> items = mapper.selectByCategoryIdAndUserId(categoryId, userId);
    if (items != null) {
      return items;
    }
    return new ArrayList<>();
  }

  @Transactional
  public boolean insert(CategoryItem item) {
    // 如果分类不属于当前用户，则无法添加记录
    if (categoryMapper.selectByIdAndUserId(item.getCategoryId(), item.getUserId()) == null) {
      return false;
    }
    String type = item.getType();
    String image = defaultImages.get(type);
    item.setImage(image);
    if (mapper.insert(item) > 0) {
      categoryMapper.addAmountById(item.getCategoryId());
      return true;
    }
    return false;
  }

  @Transactional
  public boolean delete(Long id, Long userId) {
    Long categoryId = mapper.selectCategoryId(id, userId);
    if (mapper.delete(id, userId) > 0) {
      categoryMapper.reduceAmountById(categoryId);
      return true;
    }
    return false;
  }
}
