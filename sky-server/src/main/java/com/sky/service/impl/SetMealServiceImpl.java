package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setmealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //向套餐表中插入数据
        setmealMapper.insert(setmeal);
        //获取生成的套餐id
        Long setmealID = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealID);
        });
        //保存套餐和菜品的关联关系
        setMealDishMapper.insertBatch(setmealDishes);

    }

    /**
     * 根据条件分页查询套餐信息
     *
     * @param setmealPageQueryDTO 包含分页信息和查询条件的对象
     * @return 返回分页查询结果，包括总记录数和套餐数据列表
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 提取页码和页面大小信息
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        // 启用MyBatis分页插件，设置当前页和页面大小
        PageHelper.startPage(pageNum, pageSize);
        // 执行分页查询，获取包含总记录数和数据列表的Page对象
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        // 根据查询结果构建并返回PageResult对象
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据多个ID删除套餐信息
     * 此方法首先检查每个套餐的状态，确保它们未启用如果套餐已启用，则抛出异常不允许删除
     * 对于未启用的套餐，方法将它们从数据库中删除，并同时删除与这些套餐关联的菜品信息
     *
     * @param ids 要删除的套餐的ID列表
     * @throws DeletionNotAllowedException 如果尝试删除启用的套餐，则抛出此异常
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 检查每个套餐的状态，确保它们未启用
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            // 如果套餐已启用，抛出异常不允许删除
            if (StatusConstant.ENABLE == setmeal.getStatus()) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        // 删除未启用的套餐，并同时删除与这些套餐关联的菜品信息
        ids.forEach(id -> {
            setmealMapper.deleteById(id);
            setMealDishMapper.deleteBySetmealId(id);
        });
    }

    /**
     * 根据套餐ID查询套餐信息，包括套餐基本信息和关联的菜品信息
     *
     * @param id 套餐的ID
     * @return 包含套餐信息和菜品信息的SetmealVO对象
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 查询套餐基本信息
        Setmeal setmeal = setmealMapper.getById(id);
        // 查询套餐关联的菜品信息
        List<SetmealDish> setmealDishes =setMealDishMapper.getBySetmealId(id);
        // 创建SetmealVO对象用于封装查询结果
        SetmealVO setmealVO = new SetmealVO();
        // 将套餐基本信息复制到SetmealVO对象中
        BeanUtils.copyProperties(setmeal,setmealVO);
        // 将套餐关联的菜品信息设置到SetmealVO对象中
        setmealVO.setSetmealDishes(setmealDishes);
        // 返回封装好的SetmealVO对象
        return setmealVO;
    }

    /**
     * 更新套餐信息
     *
     * @param setmealDTO 套餐DTO对象，包含套餐及其关联菜品的信息
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        // 创建一个新的套餐对象，并从DTO中复制属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        // 调用Mapper更新套餐信息
        setmealMapper.update(setmeal);

        // 获取套餐ID
        Long setmealId  = setmealDTO.getId();

        // 删除与当前套餐关联的所有菜品
        setMealDishMapper.deleteBySetmealId(setmealId);

        // 获取DTO中的关联菜品列表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        // 为每个关联菜品设置套餐ID
        setmealDishes.forEach(setmealDish->{
            setmealDish.setSetmealId(setmealId);
        });

        // 批量插入更新后的关联菜品
        setMealDishMapper.insertBatch(setmealDishes);
    }
}
