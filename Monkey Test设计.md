# 正文

## Monkey Test

一种自动化软件测试方法，通常用于发现应用程序中的潜在错误和崩溃问题。其基本思路是通过模拟随机、无序的操作来测试应用程序，以便发现异常或未处理的错误。这个名字来源于“猴子”这一形象，意味着不受控、不可预测的操作，就像让一只猴子随机点击屏幕一样。

#### 为什么要进行Monkey Test

由于多种编辑操作只调用一次接口，Monkey Test 可以通过随机化的操作模拟用户行为，从而发现哪个具体的操作导致 JSON 配置项出错。这种随机测试能够有效捕捉到那些在常规测试中可能遗漏的异常情况，有助于提高接口的稳定性和错误处理能力。

近期线上频发前端造成的切片失败报错（传递了错误的配置项），我在测试时也发现一例这样的情况，但复现难度十分大。因此Lithophane项目适合使用Monkey Test进行测试。

#### 流程

>创建项目
>
>随机操作（指定随机次数）
>
>删除项目

#### 初步设计

- **YAML 配置模块**：负责读取和解析 YAML 文件，返回对应的 XPATH 元素。
- **随机选择模块**：负责从轮盘中随机选择操作组件。
- **操作执行模块**：执行随机选择的操作并处理结果。
- **日志模块**：负责日志记录，包括成功和失败的信息。
- **Gif模块：**将每一步随机操作截屏，组成gif并保存。

# 草稿

我打算写一个monkey test框架。初步预计需要1、将元素与XPATH写成yaml文件对应，方便后续XPATH变动，不用修改源代码；2、进行随机测试的组件封装成字典的形式，键为元素实例、元素需要进行的操作、元素名；3、将随机测试组件加入到轮盘（数组）中；4、选择随机操作组件，进行字典中元素需要进行的操作，并将元素名加入到操作队列中；5、若最后运行成功，则执行删除逻辑，并清空操作队列内容，将日期和成功输出到log文件。若失败，则执行保留逻辑，将日期和操作队列和失败输出到log文件中。

### 1. XPATH与元素对应的YAML文件

使用YAML文件存储元素与其XPATH的对应关系，方便后续维护和变更。

```yaml
elements:
  login_button: '//*[@id="login-btn"]'
  submit_button: '//*[@id="submit-btn"]'
  upload_input: '//*[@id="temp-upload-btn"]'
```

### 2. 随机测试组件封装

创建一个字典，存放元素实例、操作及其名称。

```python
test_components = {
    'login': {
        'element': driver.find_element(By.XPATH, '//*[@id="login-btn"]'),
        'action': 'click',
        'name': 'Login Button'
    },
    'submit': {
        'element': driver.find_element(By.XPATH, '//*[@id="submit-btn"]'),
        'action': 'click',
        'name': 'Submit Button'
    },
    'upload': {
        'element': driver.find_element(By.XPATH, '//*[@id="temp-upload-btn"]'),
        'action': 'upload',
        'name': 'File Upload'
    }
}
```

### 3. 加入随机测试组件到轮盘

使用数组存储可随机选择的组件。

```python
import random

roulette = list(test_components.keys())
```

### 4. 随机选择操作

选择随机操作，并执行对应的动作。

```python
selected_action = random.choice(roulette)
component = test_components[selected_action]

if component['action'] == 'click':
    component['element'].click()
elif component['action'] == 'upload':
    component['element'].send_keys('path/to/file.jpg')
```

### 5. 记录操作结果

记录操作成功或失败，并将结果输出到log文件中。

```python
import logging
from datetime import datetime

# 配置日志
logging.basicConfig(filename='test_log.log', level=logging.INFO)

# 操作队列
operation_queue = []

try:
    # 假设执行了操作
    operation_queue.append(component['name'])
    logging.info(f"{datetime.now()} - {component['name']} - Success")

    # 删除逻辑
    operation_queue.clear()

except Exception as e:
    logging.error(f"{datetime.now()} - {component['name']} - Failed: {str(e)}")
```

# Monkey Test 框架设计文档

## 1. 引言

### 1.1 目的

本框架旨在提供一种有效的自动化测试方案，通过随机测试手段提高测试效率和覆盖率，支持动态的测试场景，适用于移动端和网页应用的测试。

### 1.2 背景

现有的测试框架通常依赖于静态的测试用例，难以应对频繁变化的 UI 组件和操作。Monkey Test 框架的设计将动态生成测试操作，提高测试的灵活性和准确性。

## 2. 需求分析

### 2.1 功能需求

1. 将元素与 XPATH 写成 YAML 文件对应，以方便后续的 XPATH 变动而无需修改源代码。
2. 随机测试组件封装成字典形式，键为元素实例、元素需要进行的操作、元素名。
3. 将随机测试组件加入到轮盘（数组）中，以便进行随机选择。
4. 随机选择操作组件并执行相应操作，将元素名记录到操作队列中。
5. 记录操作结果至日志，成功则执行删除逻辑，失败则保留逻辑，并输出至日志文件。

### 2.2 非功能需求

- **性能要求**：框架应具备快速响应和执行能力，支持并发测试。
- **可维护性**：代码结构应简洁明了，易于扩展和修改。

## 3. 系统设计

### 3.1 整体架构

```
+--------------------------+
|      Monkey Test 框架   |
+--------------------------+
|  YAML 配置模块          |
|  随机选择模块          |
|  操作执行模块          |
|  日志记录模块          |
+--------------------------+
```

### 3.2 模块划分

- **YAML 配置模块**：负责读取和解析 YAML 文件，返回对应的 XPATH 元素。
- **随机选择模块**：负责从轮盘中随机选择操作组件。
- **操作执行模块**：执行随机选择的操作并处理结果。
- **日志模块**：负责日志记录，包括成功和失败的信息。

## 4. 详细设计

### 4.1 数据结构

- 字典结构：

  ```python
  {
      "element_instance": "元素实例",
      "operation": "操作名称",
      "element_name": "元素名"
  }
  ```

### 4.2 核心算法

- **随机选择算法**：使用随机数生成器从轮盘中选择操作。

- 日志记录逻辑：

  - 成功时记录操作队列内容和时间戳。
- 失败时记录失败原因和当前操作队列。

## 5. 实现计划

### 5.1 时间安排

| 模块          | 开发时间 |
| ------------- | -------- |
| YAML 配置模块 | 1 周     |
| 随机选择模块  | 1 周     |
| 操作执行模块  | 2 周     |
| 日志模块      | 1 周     |
| 整合与测试    | 1 周     |

### 5.2 开发环境

- **编程语言**：Python
- **依赖库**：Selenium, PyYAML, Logging

## 6. 测试计划

### 6.1 测试用例

- 验证从 YAML 文件读取 XPATH 是否准确。
- 验证随机选择模块是否能正确选择操作。
- 验证日志记录功能的完整性和准确性。

### 6.2 性能测试

- 测试框架在高并发情况下的响应时间和资源消耗。

## 7. 总结

本框架旨在通过动态随机测试方法提升自动化测试的灵活性和效率，适应快速变化的应用环境。期待在实现过程中不断优化框架，满足日益增长的测试需求。

## 8. 附录

### 8.1 参考资料

- Selenium 官方文档
- PyYAML 官方文档

### 8.2 术语表

- **Monkey Test**：一种随机测试的自动化测试方法，用于发现潜在的缺陷。