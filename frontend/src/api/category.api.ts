
// 获取工具分类
import request from "@/utils/request.api";

export const getToolCategories = async (): Promise<string[]> => {
  const response = await request.get('/tools/categories')
  return response;
}

// 获取 API 提供商
export const getApiProviders = async (): Promise<string[]> => {
  const response = await request.get('/apis/providers')
  return response;
}

// 获取教程难度级别
export const getTutorialDifficulties = async (): Promise<string[]> => {
  const response = await request.get('/tutorials/difficulties')
  return response;
}

// 获取所有分类（通用）
export const getAllCategories = async (): Promise<string[]> => {
  const response = await request.get('/categories')
  return response;
}

// 获取标签列表
export const getTags = async (): Promise<string[]> => {
  const response = await request.get('/tags')
  return response;
}

// 获取价格类型
export const getPriceTypes = async (): Promise<string[]> => {
  const response = await request.get('/price-types')
  return response;
}

// 获取教程类型
export const getTutorialTypes = async (): Promise<string[]> => {
  const response = await request.get('/categories/getByType/tutorial')
  return response;
}

// 获取教程类型
export const getPromptsTypes = async (): Promise<string[]> => {
  const response = await request.get('/categories/getByType/prompt')
  return response;
}