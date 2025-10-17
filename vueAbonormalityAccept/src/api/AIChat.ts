import request from "@/utils/requests.ts";



const BASE_URL='http://192.168.211.158:8080'
const SUB_URL="/aichat/chatStream"


export interface Message {
    id: number | string;
    content: string;
    sender: string;
    time: Date;
    type: 'text' | 'image' | 'file';
}


export interface ChatRequest {
    message: string;
    userId: null | string;
    userName: string;
    contentOnly:boolean;
    enableGraphRag:boolean;
}



/**
 * 发送一个POST请求并以流的形式处理响应数据。一般来说调用此函数的时候不用管它的返回值，当成void函数用就可以。
 * 
 * @param {string} url - 请求的URL地址。
 * @param {Object} headers - 请求头对象，可选参数，默认为空对象。
 * @param {Object} body - 请求体对象，可选参数，默认为空对象。
 * @param {Function} hundleFunc - 处理每个数据块的回调函数。
 * @returns {Promise<string>} - 返回一个Promise，解析为完整的响应数据字符串。
 * @throws {Error} - 如果请求失败或处理过程中发生错误，将抛出错误。
 */


export const chatStream=async(msg:string,handleFunc:(data:any)=>void,enableGraphRag:boolean)=>{
    console.log("chatMsg触发")
    const token = localStorage.getItem('token')
    if(!token){
        throw new Error('未登录')
    }
    const header={
        'Authorization': `Bearer ${token}`,
    }
    const userName=localStorage.getItem('username')
    const userId = localStorage.getItem('userid')
    if(!userName){
        return
    }
    const body:ChatRequest={
        message:msg,
        userId:userId,
        userName:userName,
        contentOnly:true,
        enableGraphRag:enableGraphRag,
    }

    return postRequestStream(BASE_URL+SUB_URL,header,body,handleFunc)

}

export async function postRequestStream(url:string, headers = {}, body = {}, hundleFunc:(msg:string)=>void) {
  try {
    console.log('发送请求:',url)
    // 发送POST请求
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...headers, // 合并用户提供的请求头
      },
      body: JSON.stringify(body), // 将请求体对象转换为JSON字符串
    });

    // 检查响应状态码，如果不是成功状态码则抛出错误
    if (!response.ok) {
      throw new Error(`Request failed with status ${response.status}`);
    }

    // 获取响应体的读取器
    if(!response.body){
        return
    }
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
        // var jsonChunk = JSON.parse(cleanedChunk);
        // 调用回调函数处理每个JSON数据块
        hundleFunc(cleanedChunk);
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

function cleanData(data:string) {
  if (data.startsWith('data:')) {
    return data.substring(5); // 去掉前缀 'data:'
  }
  return data;
}

function splitData(data:string) {
  var lines = data.split(/\n+/).filter(line => line.trim() !== '');
  return lines;
}
