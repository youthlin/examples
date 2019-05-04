package com.youthlin.example.kilim;

import kilim.Mailbox;
import kilim.Pausable;
import kilim.Task;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建: youthlin.chen
 * 时间: 2019-04-20 21:50
 */
public class Battle {
    static Random rand = new Random();
    int num = 1000;
    Actor[] actors = new Actor[num];
    AtomicInteger living = new AtomicInteger(num);

    public class Actor extends Task {
        Mailbox<Integer> damage = new Mailbox<>();
        int hp = 1 + rand.nextInt(10);
        int id;

        public Actor(int id) {
            this.id = id;
        }

        @Override
        public void execute() throws Pausable, Exception {
            while (hp > 0) {
                Integer damageCount = damage.get();
                this.hp -= damageCount;
                int a = rand.nextInt(num);
                int b = rand.nextInt(num);
                int c = rand.nextInt(num);
                System.out.println("id-" + id + "\t" + hp + "\t" + damageCount + "\t" + a + "\t" + b + "\t" + c);
                actors[a].damage.putnb(1);
                actors[b].damage.putnb(1);
                actors[c].damage.putnb(1);
                Task.sleep(100);
            }
            living.decrementAndGet();
        }
    }

    void start() {
        for (int ii = 0; ii < num; ii++) {
            (actors[ii] = new Actor(ii)).start();
        }
        actors[0].damage.putb(1);
        for (int cnt, prev = num; (cnt = living.get()) > num / 2 || cnt < prev; prev = cnt, sleep()) {
            System.out.println(cnt);
        }
    }

    static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
    }

    public static void main(String[] args) {
        if (kilim.tools.Kilim.trampoline(false, args)) {
            return;
        }
        Battle battle = new Battle();
        battle.start();
        Task.idledown();
        System.out.format("\n%d actors survived the Battle Royale\n\n", battle.living.get());
    }

}
