package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseStatusEnum;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductServiceImpl.
 *
 * @author Dulk
 * @version 20180614
 * @date 2018/6/14
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArr = product.getSubImages().split(",");
                if (subImageArr.length > 0) {
                    product.setMainImage(subImageArr[0]);
                }
            }

            if (product.getId() != null) {
                if (productMapper.updateByPrimaryKeySelective(product) > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                } else {
                    return ServerResponse.createByErrorMessage("更新产品失败");
                }
            } else {
                if (productMapper.insert(product) > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                } else {
                    return ServerResponse.createByErrorMessage("新增产品失败");
                }
            }

        }

        return ServerResponse.createByErrorMessage("增加或更新产品参数不正确");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseStatusEnum.ILLEGAL_ARGUMENT, ResponseStatusEnum.ILLEGAL_ARGUMENT.getDesc()
            );
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        if (productMapper.updateByPrimaryKeySelective(product) > 0) {
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }

        return ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseStatusEnum.ILLEGAL_ARGUMENT, ResponseStatusEnum.ILLEGAL_ARGUMENT.getDesc()
            );
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVO vo = assembleProductDetailVO(product);
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVO> productListVOList = new ArrayList<ProductListVO>();
        for (Product productItem : productList) {
            ProductListVO vo = assembleProductListVO(productItem);
            productListVOList.add(vo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);

        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVO> productListVOList = new ArrayList<ProductListVO>();
        for (Product productItem : productList) {
            ProductListVO vo = assembleProductListVO(productItem);
            productListVOList.add(vo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);

        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseStatusEnum.ILLEGAL_ARGUMENT, ResponseStatusEnum.ILLEGAL_ARGUMENT.getDesc()
            );
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVO vo = assembleProductDetailVO(product);
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(
            String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseStatusEnum.ILLEGAL_ARGUMENT, ResponseStatusEnum.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }

        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArr = orderBy.split("_");
                PageHelper.orderBy(orderByArr[0] + " " + orderByArr[1]);
            }
        }

        List<Product> productList = productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(keyword) ? null : keyword,
                categoryIdList.size() == 0 ? null : categoryIdList);

        List<ProductListVO> productListVOList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVO productListVO = assembleProductListVO(product);
            productListVOList.add(productListVO);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVOList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductDetailVO assembleProductDetailVO(Product product) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setSubtitle(product.getSubtitle());
        vo.setPrice(product.getPrice());
        vo.setMainImage(product.getMainImage());
        vo.setSubImages(product.getSubImages());
        vo.setCategoryId(product.getCategoryId());
        vo.setDetail(product.getDetail());
        vo.setName(product.getName());
        vo.setStatus(product.getStatus());
        vo.setStock(product.getStock());
        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            vo.setParentCategoryId(0);
        } else {
            vo.setParentCategoryId(category.getParentId());
        }

        vo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        vo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return vo;
    }

    private ProductListVO assembleProductListVO(Product product) {
        ProductListVO vo = new ProductListVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCategoryId(product.getCategoryId());
        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        vo.setMainImage(product.getMainImage());
        vo.setPrice(product.getPrice());
        vo.setSubtitle(product.getSubtitle());
        vo.setStatus(product.getStatus());
        return vo;
    }


}
