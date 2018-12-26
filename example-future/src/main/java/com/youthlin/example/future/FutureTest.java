package com.youthlin.example.future;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 创建: youthlin.chen
 * 时间: 2018-11-27 09:41
 */
public class FutureTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureTest.class);

    private static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private static final String url = "http://baidu.com";

    public static void main(String[] args) {
        SettableFuture<String> future = SettableFuture.create();
        firstSearch(future);
        Futures.addCallback(future, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LOGGER.info("DONE");
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, EXECUTOR);
    }

    private static void firstSearch(SettableFuture<String> future) {
        Futures.addCallback(choose(url), new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LOGGER.info("first search success");
                secondSearch(result, future);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, EXECUTOR);
    }

    private static void secondSearch(String firstResult, SettableFuture<String> future) {
        ListenableFuture<String> choose = choose(url);
        Futures.addCallback(choose, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LOGGER.info("Second Success");
                future.set(firstResult + result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, EXECUTOR);
    }

    private static ListenableFuture<String> choose(String url) {
        SettableFuture<String> result = SettableFuture.create();
        int n = 5;
        List<ListenableFuture<String>> list = Lists.newArrayListWithExpectedSize(n);
        for (int i = 0; i < n; i++) {
            list.add(search(url));
        }
        ListenableFuture<List<String>> successful = Futures.successfulAsList(list);
        Futures.addCallback(successful, new FutureCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> resultList) {
                LOGGER.info("SuccessfulList");
                if (resultList != null) {
                    result.set(resultList.toString());
                } else {
                    result.set("Empty");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                LOGGER.warn("ListFail", t);
                result.setException(t);
            }
        }, EXECUTOR);
        return result;
    }

    private static ListenableFuture<String> search(String url) {
        ListenableFuture<Response> future = httpGet(url);
        return Futures.transform(future, response -> {
            if (response == null) {
                return null;
            }
            try {
                return response.getResponseBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }, EXECUTOR);
    }

    private static ListenableFuture<Response> httpGet(String url) {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
            Request request = builder.build();
            com.ning.http.client.ListenableFuture<Response> future = client.executeRequest(request);
            return new GuavaListenableFuture<>(future);
        } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
        }
    }
}
