import request from "@/utils/requests.ts";

export interface Message {
    id: number | string;
    content: string;
    sender: string;
    time: Date;
    type: 'text' | 'image' | 'file';
}


export async function postRequestStream(msg, hundleFunc) {
    try {
        // 发送POST请求
        const response = request.post('/aichat/chatStream',msg)

        // 检查响应状态码，如果不是成功状态码则抛出错误
        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        // 获取响应体的读取器
        const reader = response.body.getReader();
        // 创建一个TextDecoder实例，用于解码二进制数据为UTF-8字符串
        const decoder = new TextDecoder('utf-8');
        let data = ''; // 用于存储完整的响应数据

        // 读取响应流数据
        while (true) {
            const { done, value } = await reader.read(); // 读取下一个数据块
            if (done) {
                break; // 如果没有更多数据，结束循环
            }

            // 解码当前数据块
            var chunk = decoder.decode(value, { stream: true });
            // 将解码后的数据块按行分割
            var chunkArray = splitData(chunk);

            // 处理每个分割后的数据块
            for (var i = 0; i < chunkArray.length; i++) {
                // 清理数据块（去除多余字符等）
                var cleanedChunk = cleanData(chunkArray[i]);
                // 将清理后的数据块累加到完整数据中
                data += cleanedChunk;
                console.log(cleanedChunk); // 输出清理后的数据块

                // 如果数据块包含'[DONE]'，表示数据流结束，退出循环
                if (cleanedChunk.includes('[DONE]')) {
                    break;
                }

                // 将清理后的数据块解析为JSON对象
                var jsonChunk = JSON.parse(cleanedChunk);
                // 调用回调函数处理每个JSON数据块
                hundleFunc(j
                +sonChunk);
            }
        }

        // 返回完整的响应数据字符串
        return data;
    } catch (error) {
        // 捕获并输出错误信息
        console.error('Error during POST request:', error);
        // 抛出错误以便调用者处理
        throw error;
    }
}

function cleanData(data) {
    if (data.startsWith('data:')) {
        return data.substring(5); // 去掉前缀 'data:'
    }
    return data;
}

function splitData(data) {
    var lines = data.split(/\n+/).filter(line => line.trim() !== '');
    return lines;
}