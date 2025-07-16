package com.abnormality.abnormalityaccept.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义 MyBatis Plus 元对象处理器，用于在插入和更新操作时自动填充字段。
 *
 * <p>该处理器实现了 MetaObjectHandler 接口，主要用于在实体类属性与数据库字段自动映射过程中，
 * 对指定字段进行自动赋值处理，例如创建时间和更新时间。</p>
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 在插入操作时自动填充指定字段的值。
     *
     * <p>当前实现中，使用 strictInsertFill 方法对 "createTime" 字段进行强类型填充，
     * 填充值为当前的 LocalDateTime 时间。</p>
     *
     * <p>如果需要填充其他字段（如 createUserId），可以取消注释并调整相应代码。</p>
     *
     * @param metaObject 被操作的元对象，包含实体类及反射信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 示例：记录插入填充日志（可选）
        // log.info("开始插入填充...");

        // 使用严格模式填充 createTime 字段为当前时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 在更新操作时自动填充指定字段的值。
     *
     * <p>当前实现中，使用 strictUpdateFill 方法对 "updateTime" 字段进行强类型填充，
     * 填充值为当前的 LocalDateTime 时间。</p>
     *
     * <p>如果需要填充其他字段（如 updateUserId），可以取消注释并调整相应代码。</p>
     *
     * @param metaObject 被操作的元对象，包含实体类及反射信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 示例：记录更新填充日志（可选）
        // log.info("开始更新填充...");

        // 使用严格模式填充 updateTime 字段为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
