/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel;

/**
 * Captures the possible configuration settings for a compiler that can be
 * used when evaluating expressions.
 *
 * 捕获在评估表达式时使用的编译器的配置设置
 *
 * @author Andy Clement
 * @since 4.1
 */
public enum SpelCompilerMode {

	/**
	 *
	 * 编译器是关闭的
	 *
	 * The compiler is switched off; this is the default.
	 */
	OFF,

	/**
	 *
	 * 立即生效模式
	 *
	 * In immediate mode, expressions are compiled as soon as possible (usually after 1 interpreted run).
	 * If a compiled expression fails it will throw an exception to the caller.
	 */
	IMMEDIATE,

	/**
	 *
	 * 混合模式
	 *
	 * 表达式评估会随时间静默地在解释和编译之间切换
	 * 多次运行后，表达式将被编译
	 * 如果以后失败（可能是由于推断类型信息更改），然后将在内部捕获该错误，然后系统切换回解释模式。 以后可能会再次编译它。
	 *
	 * In mixed mode, expression evaluation silently switches between interpreted and compiled over time.
	 * After a number of runs the expression gets compiled. If it later fails (possibly due to inferred
	 * type information changing) then that will be caught internally and the system switches back to
	 * interpreted mode. It may subsequently compile it again later.
	 */
	MIXED

}
