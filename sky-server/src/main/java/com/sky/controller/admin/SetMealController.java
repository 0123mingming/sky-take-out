package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO.toString());
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 处理分页查询请求
     *
     * 该方法通过接收一个 SetmealPageQueryDTO 对象作为参数，用于指定分页查询的条件和参数
     * 使用 @GetMapping 注解指定 HTTP GET 请求的处理，@ApiOperation 注解用于 Swagger 文档生成，描述该方法的作用
     *
     * @param setmealPageQueryDTO 分页查询的条件和参数对象，包含页码、每页数量等信息
     * @return 返回一个 Result 对象，其中包含分页查询的结果 PageResult
     *         Result.success(pageResult) 表示查询成功，返回包含分页数据的 PageResult 对象
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 记录分页查询的日志信息
        log.info("分页查询：{}", setmealPageQueryDTO);
        // 调用 service 层的分页查询方法，并返回查询结果
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        // 返回包含分页查询结果的响应
        return Result.success(pageResult);

    }

    /**
     * 批量删除套餐接口
     *
     * 该接口接收一个套餐ID列表，将这些套餐从系统中删除
     * 主要用于后台管理，当需要清理多个套餐时，可以使用该接口
     *
     * @param ids 待删除的套餐ID列表，每个ID代表一个套餐
     * @return 返回删除结果，如果删除成功，返回成功结果
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        // 记录日志，追踪删除操作
        log.info("批量删除套餐：{}", ids);

        // 调用服务层方法，执行批量删除操作
        setMealService.deleteBatch(ids);

        // 返回成功结果，表示删除操作完成
        return Result.success();
    }
}
