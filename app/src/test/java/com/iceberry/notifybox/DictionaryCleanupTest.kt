/*
 * Copyright (c) 2026 Iceberry-qdd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iceberry.notifybox

import org.junit.Test
import java.io.File

/**
 * 这是一个用于清理和去重混淆字典的实用工具测试。
 * 它可以读取现有的 obfuscation-dictionary.txt，去重并按字母顺序排序，然后写回原文件。
 */
class DictionaryCleanupTest {

    @Test
    fun cleanupDictionary() {
        // 字典文件的绝对路径
        val dictionaryFile = File("D:/Users/Iceberry/Documents/AndroidStudioProjects/NotifyBox/app/obfuscation-dictionary.txt")
        
        if (!dictionaryFile.exists()) {
            println("错误：字典文件不存在于路径：${dictionaryFile.absolutePath}")
            return
        }

        println("开始清理字典：${dictionaryFile.absolutePath}")

        // 1. 读取所有行，去除空白字符
        val originalLines = dictionaryFile.readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val beforeCount = originalLines.size

        // 2. 去重并排序
        // 使用 sorted() 默认按 Unicode 顺序排序，这符合阿拉伯字母的顺序
        val uniqueLines = originalLines.distinct().sorted()

        val afterCount = uniqueLines.size

        // 3. 写回文件
        dictionaryFile.writeText(uniqueLines.joinToString("\n"))

        println("清理完成！")
        println("处理前行数: $beforeCount")
        println("处理后行数: $afterCount (移除了 ${beforeCount - afterCount} 个重复项)")
        
        // 打印前10个作为预览
        println("\n前10个条目预览：")
        uniqueLines.take(10).forEach { println(it) }
    }
}
