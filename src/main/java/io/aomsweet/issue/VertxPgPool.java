package io.aomsweet.issue;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author aomsweet
 */
public class VertxPgPool {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setIdleTimeout(1)
                .setIdleTimeoutUnit(TimeUnit.MINUTES)
                .setTcpKeepAlive(true)
                .setHost("192.168.0.200")
                .setDatabase("postgres")
                .setUser("postgres")
                .setPassword("fdsa888");

        PoolOptions poolOptions = new PoolOptions()
                .setIdleTimeout(1)
                .setIdleTimeoutUnit(TimeUnit.MINUTES)
                .setConnectionTimeout(1)
                .setConnectionTimeoutUnit(TimeUnit.MINUTES)
                // MaxWaitQueueSize
                .setMaxWaitQueueSize(Integer.MAX_VALUE)
                // MAXSIZE
                .setMaxSize(8);

        // Create the client pool
        PgPool pool = (PgPool) PgPool.client(vertx, connectOptions, poolOptions);

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
