declare class JavaDao<T> {
    /**
     * 向表中新增数据
     * @param model
     *      数据对象模型实体
     */
    insert(model: T): void;

    /**
     * 根据id查询数据
     * @param id
     *     数据模型id
     * @returns
     *      数据模型实体
     */
    getById(id: number): T;

    /**
     * 根据条件查询单条数据
     * @param where
     *      查询条件
     *      如：Where.where(MF.User._id, C.EQ, 1)
     * @returns
     *      数据模型实体
     */
    get(where: Where): T;

    /**
     * 根据id删除数据
     * @param id
     *      数据模型id
     */
    deleteById(id: number): void;

    /**
     * 根据条件删除数据
     * @param where
     *      删除条件
     *      如：Where.where(MF.User._id, C.EQ, 1)
     */
    delete(where: Where): void;

    /**
     * 根据id更新数据
     * @param model
     *      数据模型实体
     */
    update(model: T): void;

    /**
     * 根据条件查询数据列表
     * @param where
     *      查询条件
     *      如：Where.where(MF.User._id, C.EQ, 1)
     */
    list(where: Where): T[];
    /**
     * 根据条件分页查询数据
     * @param where
     *      查询条件
     *      如：Where.where(MF.User._id, C.EQ, 1)
     * @param page
     *      页码
     * @param pageSize
     *      每页数据量
     * @returns
     *      数据模型实体列表
     */
    listPage(where: Where, page: number, pageSize: number): ResultPage<T>;
}

declare class ResultPage<T> {
    /**
     * 当前页的数据列表
     */
    list: T[];

    /**
     * 总记录数
     */
    totalSize: number;

    /**
     * 每页数据量
     */
    pageSize: number;

    /**
     * 页码
     */
    pageNum: number;

    /**
     * 总页数
     */
    totalPage: number;
}

declare class Where {
    /**
     * 构建一个空的查询条件
     */
    static emptyWhere(): Where;

    /**
     * 构建一个查询条件
     */
    static where(field: MF, symbol: C, value: any): Where;

    /**
     * 构建一个AND查询条件
     * @param field
     *      字段
     * @param symbol
     *      比较符
     * @param value
     *      比较值
     */
    and(field: MF, symbol: C, value: any): Where;

    /**
     * 构建一个OR查询条件
     * @param field
     *      字段
     * @param symbol
     *      比较符
     * @param value
     *      比较值
     */
    or(field: MF, symbol: C, value: any): Where;

    /**
     * 构建一个嵌套的AND查询条件
     * @param where
     *      嵌套的查询条件
     */
    and(where: Where): Where;

    /**
     * 构建一个嵌套的OR查询条件
     * @param where
     */
    or(where: Where): Where;

}

/**
 * 比较符
 */
declare enum C {
    /**
     * =
     */
    EQ = "=",
    /**
     * <>
     */
    NE = "<>",
    /**
     * >
     */
    GT = ">",
    /**
     * <
     */
    LT = "<",
    /**
     * >=
     */
    GE = ">=",
    /**
     * <=
     */
    LE = "<=",

    LIKE = 'Contain',

    IN = 'In List'
}

/**
 * 消息对象
 */
declare class Message {
    /**
     * 消息内容
     */
    content: string | MessageContent[];

    /**
     * 消息所属角色
     */
    role: 'user' | 'system' | 'assistant' | 'tool';
}

/**
 * 消息内容
 */
declare class MessageContent {
    /**
     * 消息类型
     */
    type: 'text' | 'image_url' | 'audio_url' | 'video_url' | 'file_url' | 'file';

    /**
     * 文本消息内容
     */
    text?: string;

    /**
     * 图片消息内容
     */
    image_url?: FileUrl;

    /**
     * 音频消息内容
     */
    audio_url?: FileUrl;

    /**
     * 视频消息内容
     */
    video_url?: FileUrl;

    /**
     * 文件消息内容
     */
    file_url?: FileUrl;
}

/**
 * 文件的URL对象
 */
declare class FileUrl {
    /**
     * 文件的URL地址
     */
    url: string;
}

/**
 * 模型参数选项
 */
declare class ModelOption {

    /**
     * 是否使用流式生成文本
     */
    stream: boolean;

    /**
     * 模型名称, 与模型API端点的model属性对应
     */
    model: string;

    /**
     * 生成文本的温度，范围是0到1
     * @default 0.7
     */
    temperature?: number;

    /**
     * 生成文本的最大长度
     * @default 4096
     */
    max_tokens?: number;

    /**
     * 生成文本的top_p值，范围是0到1
     * @default 1.0
     */
    top_p?: number;

    /**
     * 生成文本的频率惩罚，范围是-2.0到2.0
     * @default 0.0
     */
    frequency_penalty?: number;

    /**
     * 生成文本的存在惩罚，范围是-2.0到2.0
     * @default 0.0
     */
    presence_penalty?: number;

    /**
     * 是否返回生成文本的token信息
     * @default false
     */
    return_tokens?: boolean;

    /**
     * 是否返回生成文本的logprobs信息
     * @default false
     */
    return_logprobs?: boolean;

    /**
     * 是否返回生成文本的stop信息
     * @default false
     */
    return_stop?: boolean;

    /**
     * 是否返回生成文本的usage信息
     * @default false
     */
    return_usage?: boolean;
}

/**
 * 模型API属性信息
 */
declare interface ModelApiAttribute {
    /**
     * 监听的模型名称
     */
    model: string;
    /**
     * 监听的API端点路径
     */
    path: string;
    /**
     * 权重, 相同API路径和模型名称的API端点下, 权重值越大, 优先级越高, 当权重<= 0时, 该API端点不参与模型API的调用
     */
    widget: number;
    /**
     * API 端点的服务方法
     * @param messages
     *      接收到的消息列表
     * @param option
     *      接收到的模型参数选项
     */
    service: (messages: Message[], option: ModelOption) => void;
}

/**
 * 模型API端点
 */
declare class ApiEndpoint {
    constructor(attribute: ModelApiAttribute);
}

/**
 * HTTP请求发构建工具
 */
declare class HTTP {
    /**
     * 构建HTTP发送请求
     * @param url
     *      请求的URL地址
     * @param options
     *      请求参数
     */
    static request(url: string, options?: HttpOption): HttpSender;
}

/**
 * HTTP请求参数
 */
declare class HttpOption{
    /**
     * 请求方法
     */
    method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'HEAD' | 'OPTIONS';
    /**
     * 请求的body内容
     */
    headers?: Record<string, string>;
    /**
     * 请求的body内容
     */
    body: string | object;
}

/**
 * HTTP请求发送器
 */
declare interface HttpSender {
    /**
     * 发送流式请求
     * @param handler
     *     处理流式响应的回调函数
     *     @param handler.done
     *     是否已完成响应
     *     @param handler.data
     *     响应数据
     */
    stream(
        handler: (
            done: boolean,
            data: string
        ) => void
    ): void;
}

/**
 * Server-Sent Events (SSE) 事件发送器
 */
declare class SSE {

    /**
     * 发送OpenAI消息格式的SSE事件
     * @param message
     *      消息内容
     */
    static sendOpenAIMessage(message: string): void;

    /**
     * 发送OpenAI消息格式的SSE事件
     * @param role
     *      消息所属角色
     * @param content
     *      消息内容
     * @param model
     *      模型名称
     */
    static sendOpenAIMessage(role: 'user' | 'assistant' | 'system', content: string, model: string): void;

    /**
     * 发送自定义对象的SSE事件
     * @param obj
     *      自定义对象
     */
    static sendObject(obj: any): void;

    /**
     * 发送SSE结束事件
     */
    static done(): void;

    /**
     * 发送OpenAI使用情况统计信息
     * @param usage
     *      使用情况统计信息
     */
    static sendOpenAIUsage(usage: {
        promptTokens: number;
        completionTokens: number;
        totalTokens?: number;
    }): void;
}

/**
 * 存储工具类
 * 用于存储和获取数据, 已存储的数据不会随着API调用结束而销毁. 作用域为当前API节点
 */
declare class EndpointStorage {
    /**
     * 获取存储的值
     * @param key
     *      存储的键
     * @returns
     *      存储的值
     */
    static get(key: string): any;

    /**
     * 设置存储的值
     * @param key
     *      存储的键
     * @param value
     *      存储的值
     * @param expire
     *      过期时间, 单位为毫秒, 0表示不过期
     */
    static set(key: string, value: any, expire: number): void;

    /**
     * 删除存储的值
     * @param key
     *      存储的键
     */
    static remove(key: string): void;

    /**
     * 检查某值是否存在
     */
    static containsKey(key: string): boolean;

    /**
     * 清除所有存储的值
     */
    static clear(): void;
}

