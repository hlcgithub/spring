/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

/**
 * Interface to be implemented by objects used within a {@link BeanFactory} which
 * are themselves factories for individual objects. If a bean implements this
 * interface, it is used as a factory for an object to expose, not directly as a
 * bean instance that will be exposed itself.
 *
 * 该接口会被在BeanFactory中使用的对象实现，这些接口对于个别的对象而言，它们是自己的工厂
 * 如果一个bean实现该接口，那么这个bean用作一个让对象暴露的工厂，不会直接看做暴露自己的bean实例
 *
 * <p><b>NB: A bean that implements this interface cannot be used as a normal bean.</b>
 * A FactoryBean is defined in a bean style, but the object exposed for bean
 * references ({@link #getObject()}) is always the object that it creates.
 *
 * 实现这个接口的bean，不会当成一个普通的bean来使用
 *一个FactoryBean是以bean的样式定义，getObject方法返回的引用是FactoryBean创建的
 *
 * <p>FactoryBeans can support singletons and prototypes, and can either create
 * objects lazily on demand or eagerly on startup. The {@link SmartFactoryBean}
 * interface allows for exposing more fine-grained behavioral metadata.
 *
 * FactoryBean支持单例和prototype，并且可以创建lazy对象或者是及时加载的对象。SmartFactoryBean接口允许暴露更细粒度的行为元数据
 *
 * <p>This interface is heavily used within the framework itself, for example for
 * the AOP {@link org.springframework.aop.framework.ProxyFactoryBean} or the
 * {@link org.springframework.jndi.JndiObjectFactoryBean}. It can be used for
 * custom components as well; however, this is only common for infrastructure code.
 *
 * 这个接口在框架内部大量使用，比如AOP ProxyFactoryBean或者JndiObjectFactoryBean。它也可用于自定义的组件。然而这只会在基础代码中常见
 *
 * <p><b>{@code FactoryBean} is a programmatic contract. Implementations are not
 * supposed to rely on annotation-driven injection or other reflective facilities.</b>
 * {@link #getObjectType()} {@link #getObject()} invocations may arrive early in the
 * bootstrap process, even ahead of any post-processor setup. If you need access to
 * other beans, implement {@link BeanFactoryAware} and obtain them programmatically.
 *
 * FactoryBean是一个编码规范。实现类不应该依赖于注解注入或者其他反射工具。getObjectType、getObject方法可能比bootstrap进程更早执行，甚至早于任何的post processor启动
 * 如果你想访问其他bean，可以实现BeanFactoryAware接口得到他们。
 *
 * <p><b>The container is only responsible for managing the lifecycle of the FactoryBean
 * instance, not the lifecycle of the objects created by the FactoryBean.</b> Therefore,
 * a destroy method on an exposed bean object (such as {@link java.io.Closeable#close()}
 * will <i>not</i> be called automatically. Instead, a FactoryBean should implement
 * {@link DisposableBean} and delegate any such close call to the underlying object.
 *
 * 这个容器只负责管理FactoryBean实例的生命周期，而不是由FactoryBean创建的对象的生命周期。因此，一个暴露的bean对象（比如Closeable的close方法）的destroy方法不会被自动调用
 * 相反，一个FactoryBean应该实现DisposableBean，并且将类似close的调用委托给底层的对象
 *
 * <p>Finally, FactoryBean objects participate in the containing BeanFactory's
 * synchronization of bean creation. There is usually no need for internal
 * synchronization other than for purposes of lazy initialization within the
 * FactoryBean itself (or the like).
 *
 * 最后，FactoryBean对象会参与中bean创建的同步操作中，一般不需要内部同步，除非FactoryBean自己想延迟初始化
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 08.03.2003
 * @param <T> the bean type
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 */
public interface FactoryBean<T> {

	/**
	 * The name of an attribute that can be
	 * {@link org.springframework.core.AttributeAccessor#setAttribute set} on a
	 * {@link org.springframework.beans.factory.config.BeanDefinition} so that
	 * factory beans can signal their object type when it can't be deduced from
	 * the factory bean class.
	 *
	 * 在BeanDefinition中设置的属性名，因此在factory bean无法推断其类型时，factory beans能表明他们的类型
	 * @since 5.2
	 */
	String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";


	/**
	 * 返回一个被这个工厂管理的实例，这个实例可能是shared或者independent
	 * 和BeanFactory一样，它同样支持单例和prototype
	 * 如果在FactoryBean没有完成初始化调用这个方法，会抛出FactoryBeanNotInitializedException，比如涉及到循环引用
	 * spring 2.0，FactoryBeans可以返回null对象，工厂会将此作为正常值使用，在这种情况下，不再会抛出FactoryBeanNotInitializedException
	 * 现在推荐FactoryBean实现类抛出FactoryBeanNotInitializedException
	 *
	 * Return an instance (possibly shared or independent) of the object
	 * managed by this factory.
	 * <p>As with a {@link BeanFactory}, this allows support for both the
	 * Singleton and Prototype design pattern.
	 * <p>If this FactoryBean is not fully initialized yet at the time of
	 * the call (for example because it is involved in a circular reference),
	 * throw a corresponding {@link FactoryBeanNotInitializedException}.
	 * <p>As of Spring 2.0, FactoryBeans are allowed to return {@code null}
	 * objects. The factory will consider this as normal value to be used; it
	 * will not throw a FactoryBeanNotInitializedException in this case anymore.
	 * FactoryBean implementations are encouraged to throw
	 * FactoryBeanNotInitializedException themselves now, as appropriate.
	 * @return an instance of the bean (can be {@code null})
	 * @throws Exception in case of creation errors
	 * @see FactoryBeanNotInitializedException
	 */
	@Nullable
	T getObject() throws Exception;

	/**
	 * 返回被这个工厂创建的对象的类型，如果事先不知道会返回null
	 * 这样可以在对象没实例化，检查对象的特定类型，比如在autowire的时候
	 * 在创建单例的实现中，应该尽量避免去创建单例对象，应该提前评估类型
	 * 对于prototype，同样建议返回一个有意义的类型。在工厂初始化完成之前这个方法可以被调用
	 * 在初始化期间，不要依赖状态，如果合适可以使用这个状态
	 * Autowiring会忽略为null的FactoryBean。因此，强烈建议实现这个方法，使用FactoryBean的正确状态
	 *
	 * Return the type of object that this FactoryBean creates,
	 * or {@code null} if not known in advance.
	 * <p>This allows one to check for specific types of beans without
	 * instantiating objects, for example on autowiring.
	 * <p>In the case of implementations that are creating a singleton object,
	 * this method should try to avoid singleton creation as far as possible;
	 * it should rather estimate the type in advance.
	 * For prototypes, returning a meaningful type here is advisable too.
	 * <p>This method can be called <i>before</i> this FactoryBean has
	 * been fully initialized. It must not rely on state created during
	 * initialization; of course, it can still use such state if available.
	 * <p><b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return
	 * {@code null} here. Therefore it is highly recommended to implement
	 * this method properly, using the current state of the FactoryBean.
	 * @return the type of object that this FactoryBean creates,
	 * or {@code null} if not known at the time of the call
	 * @see ListableBeanFactory#getBeansOfType
	 */
	@Nullable
	Class<?> getObjectType();

	/**
	 * 被这个工厂管理的对象是否是singleton的，也就是说getObject方法会返回同一个对象（一个被缓存的引用）
	 * 如果FactoryBean有singleton对象，getObject方法返回的对象可能是从这个FactoryBean的缓存中取的
	 * 因此，这个方法不会返回true，除非这个工厂暴露的是同一引用。
	 * FactoryBean自己的单例状态是由其所属的BeanFactory提供的。通常情况下，这个状态是定义成单例
	 * 这个方法返回false并表示返回的对象是independent实例。
	 * SmartFactoryBean接口的实现类，可以通过其isPrototype方法判断其为independent实例
	 * 如果isSingleton方法的实现返回false，那么没实现SmartFactoryBean接口的FactoryBean实现类会简单地认为是independent，这个方法默认实现返回true，因为FactoryBean通常是管理singleton实例
	 *
	 * Is the object managed by this factory a singleton? That is,
	 * will {@link #getObject()} always return the same object
	 * (a reference that can be cached)?
	 * <p><b>NOTE:</b> If a FactoryBean indicates to hold a singleton object,
	 * the object returned from {@code getObject()} might get cached
	 * by the owning BeanFactory. Hence, do not return {@code true}
	 * unless the FactoryBean always exposes the same reference.
	 * <p>The singleton status of the FactoryBean itself will generally
	 * be provided by the owning BeanFactory; usually, it has to be
	 * defined as singleton there.
	 * <p><b>NOTE:</b> This method returning {@code false} does not
	 * necessarily indicate that returned objects are independent instances.
	 * An implementation of the extended {@link SmartFactoryBean} interface
	 * may explicitly indicate independent instances through its
	 * {@link SmartFactoryBean#isPrototype()} method. Plain {@link FactoryBean}
	 * implementations which do not implement this extended interface are
	 * simply assumed to always return independent instances if the
	 * {@code isSingleton()} implementation returns {@code false}.
	 * <p>The default implementation returns {@code true}, since a
	 * {@code FactoryBean} typically manages a singleton instance.
	 * @return whether the exposed object is a singleton
	 * @see #getObject()
	 * @see SmartFactoryBean#isPrototype()
	 */
	default boolean isSingleton() {
		return true;
	}

}
