# TaskScheduler for Android

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-9%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![Download](https://api.bintray.com/packages/dsiner/maven/taskscheduler/images/download.svg) ](https://bintray.com/dsiner/maven/taskscheduler/_latestVersion)

> TaskScheduler is a library for composing asynchronous and event-based programs by using observable sequences.

## Set up
Maven:
```xml
<dependency>
  <groupId>com.dsiner.lib</groupId>
  <artifactId>taskscheduler</artifactId>
  <version>1.0.2</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.dsiner.lib:taskscheduler:1.0.2'
```

## Getting started

Execute sync task in main thread

```java
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                ...do something in main thread
            }
        });
```

Execute async task in cached thread pool

```java
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                ...do something in asynchronous thread
            }
        });
```

Execute async task in single thread pool

```java
        TaskScheduler.executeSingle(new Runnable() {
            @Override
            public void run() {
                ...do something in asynchronous thread
            }
        });
```

Execute async task in a new thread

```java
        TaskScheduler.executeNew(new Runnable() {
            @Override
            public void run() {
                ...do something in asynchronous thread
            }
        });
```

Create task

```java
        TaskScheduler.create(new Task<List<String>>() {
            @Override
            public List<String> run() {
                ...do something in io thread
                return new ArrayList<>();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Function<List<String>, String>() {
                    @Override
                    public String apply(@NonNull List<String> strings) throws Exception {
                        ...do something in a new thread, such as time-consuming, map conversion, etc.
                        return "";
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull String s) throws Exception {
                        ...do something in io thread, such as time-consuming, map conversion, etc.
                        return true;
                    }
                })
                ...
                .observeOn(Schedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        ...do something in main thread
                    }

                    @Override
                    public void onError(Throwable e) {
                        ...do something in main thread
                    }
                });
```

## Latest Changes
- [Changelog.md](CHANGELOG.md)

## Licence

```txt
Copyright 2018 D

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
