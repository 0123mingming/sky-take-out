package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info ("新增菜品: {}",dishDTO.toString());
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    @GetMapping ("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}", dishPageQueryDTO);
       PageResult pageResult =dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据菜品ID查询菜品信息
     * 此接口用于获取特定菜品的详细信息，包括菜品的基本信息和口味信息
     * 通过URL路径参数传递菜品ID，使用GET HTTP方法调用
     *
     * @param id 菜品ID，用于指定需要查询的菜品
     * @return 返回包含菜品信息的Result对象，其中DishVO为菜品信息的载体
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        // 记录查询菜品的日志，包括菜品ID
        log.info("根据id查询菜品：{}", id);

        // 调用服务层方法，根据ID获取包含口味信息的菜品对象
        DishVO dishVO=dishService.getByIdWithFlavor(id);

        // 返回成功结果，包含查询到的菜品信息
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation("修改菜品")
    /**
     * 修改菜品信息的接口方法
     *
     * @param dishDTO 菜品信息的DTO对象，包含要修改的菜品信息
     * @return 返回修改结果，成功则返回成功结果
     */
    public Result update(@RequestBody DishDTO dishDTO){
        // 记录修改菜品信息的日志
        log.info("修改菜品信息：{}", dishDTO);

        // 调用服务层方法，更新菜品信息，包括口味等关联信息
        dishService.updateWithFlavor(dishDTO);

        // 返回成功结果
        return Result.success();
    }

    /**
     * 根据分类ID查询菜品列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> list=dishService.list(categoryId);
        return Result.success(list);
    }
}
