##  `IoC`

​	**`IoC`（Inversion of Control:控制反转）** 是一种设计思想，而不是一个具体的技术实现。`IoC `的思想就是将原本在程序中手动创建对象的控制权，交由 Spring 框架来管理。不过， `IoC `并非 Spring 特有，在其他语言中也有应用。

**使用`IoC`的优点：**

1. 对象之间的耦合度或者说依赖程度降低；
2. 资源变的容易管理；比如你用 Spring 容器提供的话很容易就可以实现一个单例。

### Spring Bean

Bean 代指的就是那些被 IoC 容器所管理的对象。

#### 循环依赖如何解决

[动画学Spring如何用三级缓存解决循环依赖_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1AJ4m157MU/?spm_id_from=333.337.search-card.all.click&vd_source=0c88167e5094278031b1523f6166b389)