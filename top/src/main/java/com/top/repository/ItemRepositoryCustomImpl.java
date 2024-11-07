package com.top.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.top.constant.ItemSellStatus;
import com.top.dto.ItemSearchDto;
import com.top.dto.MainItemDto;
import com.top.dto.QMainItemDto;
import com.top.entity.Item;
import com.top.entity.QItem;
import com.top.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    // 1107 성아 카테고리 검색 추가
    private BooleanExpression searchCategoryEq(Long category) {
        if (category == null) {
            return null;
        }
        return QItem.item.category.eq(category);
    }

    // 1107 성아 메인페이지 정렬 조건 추가
    private OrderSpecifier<?> getSortOrder(String sortCondition) {
        if ("priceDesc".equals(sortCondition)) {
            return QItem.item.price.desc(); // 가격 높은 순
        } else if ("priceAsc".equals(sortCondition)) {
            return QItem.item.price.asc(); // 가격 낮은 순
        } else if ("newest".equals(sortCondition)) {
            return QItem.item.regTime.desc(); // 최신순
        } else if ("oldest".equals(sortCondition)) {
            return QItem.item.regTime.asc(); // 오래된순
        }
        return QItem.item.no.desc(); // 기본 정렬: 최신순(내림차순)
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()),
                        searchCategoryEq(itemSearchDto.getCategory())  // 1107 성아 카테고리 조건 추가
                )
                .orderBy(QItem.item.no.desc()) // 1101 성아 수정
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()),
                        searchCategoryEq(itemSearchDto.getCategory())  // 1107 성아 카테고리 조건 추가
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        //10-18 은열추가
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(itemImg.repimgYn.eq("Y"));
        // ItemSellStatus 조건 추가
        if (itemSearchDto.getSearchSellStatus() != null) {
            builder.and(item.itemSellStatus.eq(ItemSellStatus.SELL)
                    .or(item.itemSellStatus.eq(ItemSellStatus.SOLD_OUT)));
        }
        builder.and(itemNmLike(itemSearchDto.getSearchQuery()));
        //10-18은열 추가
        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item.no, // 1101 성아 수정
                                item.category,
                                item.itemSellStatus,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(builder)//은열 1018 수정
                //                .where(itemImg.repimgYn.eq("Y"))
                //                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(getSortOrder(itemSearchDto.getSortCondition())) // 1107 성아 정렬 조건 수정
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getCateItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        //10-18 은열추가
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(itemImg.repimgYn.eq("Y"));
        // ItemSellStatus 조건 추가
        if (itemSearchDto.getSearchSellStatus() != null) {
            builder.and(item.itemSellStatus.eq(ItemSellStatus.SELL)
                    .or(item.itemSellStatus.eq(ItemSellStatus.SOLD_OUT)));
        }
        //241022 은열 카테고리 조건 추가
        if (itemSearchDto.getCategory() != null) {
            builder.and(item.category.eq(itemSearchDto.getCategory()));
        }

        builder.and(itemNmLike(itemSearchDto.getSearchQuery()));
        //10-18은열 추가
        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item.no, // 1101 성아 수정
                                item.category,
                                item.itemSellStatus,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(builder)//은열 1018 수정
                //                .where(itemImg.repimgYn.eq("Y"))
                //                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.no.desc()) // 1101 성아 수정
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }

}