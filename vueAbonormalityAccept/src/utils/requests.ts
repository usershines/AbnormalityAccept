import axios, { AxiosError } from 'axios'
import type {
    AxiosInstance,
    InternalAxiosRequestConfig,
    AxiosRequestConfig,
    AxiosResponse,
} from 'axios'
import router from "@/router";

/**
 * 定义后端返回的通用响应结构
 * @template T 数据类型泛型
 */
interface ResponseData<T = any> {
    code: number      // 业务状态码
    msg: string   // 业务提示信息
    data: T           // 实际业务数据
}

/**
 * 封装Axios的请求类，提供统一的拦截器处理和请求方法
 */
class Request {
    private instance: AxiosInstance

    /**
     * 创建Request实例
     * @param config Axios基础配置
     */
    constructor(config: AxiosRequestConfig) {
        this.instance = axios.create(config)

        // 请求拦截器：统一处理请求前的操作（如添加token）
        this.instance.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                // 从本地存储获取token并添加到请求头
                const token = localStorage.getItem('token')
                if (token) {
                    config.headers = config.headers || {}
                    config.headers.Authorization = `Bearer ${token}`
                }
                return config
            },
            (error: AxiosError) => {
                return Promise.reject(error)
            },
        )

        // 响应拦截器：统一处理响应数据和错误
        this.instance.interceptors.response.use(
            (response: AxiosResponse) => {
                const res = response.data as ResponseData
                // 业务状态码非200时视为业务错误
                if (res.code !== 200) {
                    console.error('业务错误: ',res.msg)
                    if(res.code == 403) {
                        console.log('登出')
                        router.push("/")
                    }else {
                        return Promise.reject(res)
                    }
                }
                // 返回响应数据体（包含code/message/data结构）
                console.log('响应数据:')
                console.log(response.data)
                return response.data
            },
            (error: AxiosError) => {
                // 根据HTTP状态码处理系统级错误
                if (error.response) {
                    switch (error.response.status) {
                        case 401:
                            console.error('未授权，跳转登录')
                            // router.push('/login');
                            break
                        case 403:
                            console.error('权限不足')
                            break
                        case 500:
                            console.error('服务器错误')
                            break
                    }
                }
                return Promise.reject(error)
            },
        )
    }
    /**
     * 通用请求方法
     * @template T 响应数据类型
     * @param config 请求配置
     * @returns 响应数据
     */
    public request<T = any>(config: AxiosRequestConfig): Promise<T> {
        return this.instance.request(config)
    }

    /**
     * GET请求
     * @template T 响应数据类型
     * @param url 请求地址
     * @param config 请求配置
     * @returns 响应数据
     */
    // 不定长参数
    public get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return this.instance.get(url, config)
    }

    /**
     * POST请求
     * @template T 响应数据类型
     * @param url 请求地址
     * @param data 请求体数据
     * @param config 请求配置
     * @returns 响应数据
     */
    public post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        return this.instance.post(url, data, config)
    }

    /**
     * PUT请求
     * @template T 响应数据类型
     * @param url 请求地址
     * @param data 请求体数据
     * @param config 请求配置
     * @returns 响应数据
     */
    public put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        return this.instance.put(url, data, config)
    }

    /**
     * DELETE请求
     * @template T 响应数据类型
     * @param url 请求地址
     * @param config 请求配置
     * @returns 响应数据
     */
    public delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return this.instance.delete(url, config)
    }


}

// 创建全局请求实例（单例模式）
const request = new Request({
    baseURL: '/api', // API基础路径
    timeout: 10000,  // 请求超时时间(毫秒)
    headers: { 'Content-Type': 'application/json' }, // 默认请求头
})

export default request
