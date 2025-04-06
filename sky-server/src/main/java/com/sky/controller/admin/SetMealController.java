package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
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
    private SetmealService setMealService;

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
    /**
     * 根据ID查询套餐信息
     *
     * 此接口用于通过特定的套餐ID来查询套餐的详细信息，包括套餐内的菜品信息
     * 它使用GET请求方式，并通过路径变量{id}来指定要查询的套餐ID
     *
     * @param id 套餐ID，用于指定需要查询的套餐
     * @return 返回包含套餐信息的Result对象，其中包含套餐的详细信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        // 调用服务层方法，通过ID获取套餐信息，包括套餐内的菜品信息
        SetmealVO setmealVO = setMealService.getByIdWithDish(id);
        // 返回成功结果，包含查询到的套餐信息
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        // 记录日志，追踪更新操作
        log.info("更新套餐：{}", setmealDTO);
        // 调用服务层方法，更新套餐信息
        setMealService.update(setmealDTO);
        // 返回成功结果，表示更新操作完成
        return Result.success();
    }
    /**
     * 更新套餐的销售状态
     *
     * @param status 套餐的销售状态，1表示起售，0表示停售
     * @param id 套餐的唯一标识符
     * @return 返回操作结果，成功则返回成功信息
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售套餐")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setMealService.startOrStop(status, id);
        return Result.success();
    }
}
