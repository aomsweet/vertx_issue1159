package io.aomsweet.issue;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author aomsweet
 */
public class VertxJdbcPool {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        String url = "jdbc:postgresql://192.168.0.200:5432/postgres";
        JDBCPool pool = JDBCPool.pool(vertx,
                new JDBCConnectOptions()
                        .setJdbcUrl(url)
                        .setUser("postgres")
                        .setPassword("fdsa888"),
                new PoolOptions()
                        .setMaxSize(8)
                        .setMaxWaitQueueSize(2000)
                        .setIdleTimeout(1)
                        .setIdleTimeoutUnit(TimeUnit.MINUTES)
                        .setConnectionTimeout(1)
                        .setConnectionTimeoutUnit(TimeUnit.MINUTES)
        );

        List<Future> list = new ArrayList<>();
        // Concurrent open transactions
        for (int i = 0; i < 100; i++) {
            list.add(pool.withTransaction(conn -> {
                System.out.println(conn);
                return Future.succeededFuture();
            }).onFailure(Throwable::printStackTrace));
        }

        CompositeFuture.all(list).onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("ok!");
            } else {
                ar.cause().printStackTrace();
            }
            vertx.close();
        });
    }

}
