/*
 * Copyright 2016-2019 David Karnok
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

package hu.akarnokd.rxjava2.operators;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import org.junit.Test;

import hu.akarnokd.rxjava2.test.*;
import io.reactivex.*;
import io.reactivex.functions.Function;
import io.reactivex.subjects.MaybeSubject;

public class MaybeFlatMapSignalSingleTest {

    @Test
    public void normalEmpty() {
        final int[] counts = { 0, 0, 0 };

        Maybe.<Integer>empty()
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        return Single.just(0);
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        return Single.error(e);
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        return Single.just(1);
                    }
                }
        ))
        .test()
        .assertResult(1);

        assertEquals(0, counts[0]);
        assertEquals(0, counts[1]);
        assertEquals(1, counts[2]);
    }

    @Test
    public void normalSuccess() {
        final int[] counts = { 0, 0, 0 };

        Maybe.just(0)
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        return Single.just(2);
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        return Single.error(e);
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        return Single.just(1);
                    }
                }
        ))
        .test()
        .assertResult(2);

        assertEquals(1, counts[0]);
        assertEquals(0, counts[1]);
        assertEquals(0, counts[2]);
    }

    @Test
    public void empty() {
        final int[] counts = { 0, 0, 0 };

        Maybe.<Integer>empty()
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        return Single.just(1);
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        return Single.error(e);
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        return Single.just(2);
                    }
                }
        ))
        .test()
        .assertResult(2);

        assertEquals(0, counts[0]);
        assertEquals(0, counts[1]);
        assertEquals(1, counts[2]);
    }

    @Test
    public void error() {
        final int[] counts = { 0, 0, 0 };

        Maybe.<Integer>error(new TestException())
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        return Single.just(2);
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        return Single.error(e);
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        return Single.just(3);
                    }
                }
        ))
        .test()
        .assertFailure(TestException.class);

        assertEquals(0, counts[0]);
        assertEquals(1, counts[1]);
        assertEquals(0, counts[2]);
    }

    @Test
    public void normalSuccessCrash() {
        final int[] counts = { 0, 0, 0 };

        Maybe.just(1)
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        throw new TestException("onSuccess");
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        throw new TestException("onError");
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        throw new TestException("onComplete");
                    }
                }
        ))
        .test()
        .assertFailureAndMessage(TestException.class, "onSuccess");

        assertEquals(1, counts[0]);
        assertEquals(0, counts[1]);
        assertEquals(0, counts[2]);
    }

    @Test
    public void normalEmptyCrash() {
        final int[] counts = { 0, 0, 0 };

        Maybe.<Integer>empty()
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        throw new TestException("onSuccess");
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        throw new TestException("onError");
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        throw new TestException("onComplete");
                    }
                }
        ))
        .test()
        .assertFailureAndMessage(TestException.class, "onComplete");

        assertEquals(0, counts[0]);
        assertEquals(0, counts[1]);
        assertEquals(1, counts[2]);
    }

    @Test
    public void errorCrash() {
        final int[] counts = { 0, 0, 0 };

        Maybe.<Integer>error(new TestException("main"))
        .as(Maybes.flatMapSingle(
                new Function<Integer, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Integer e)
                            throws Exception {
                        counts[0]++;
                        throw new TestException("onSuccess");
                    }
                },
                new Function<Throwable, Single<Integer>>() {
                    @Override
                    public Single<Integer> apply(Throwable e)
                            throws Exception {
                        counts[1]++;
                        throw new TestException("onError");
                    }
                },
                new Callable<Single<Integer>>() {
                    @Override
                    public Single<Integer> call() throws Exception {
                        counts[2]++;
                        throw new TestException("onComplete");
                    }
                }
        ))
        .test()
        .assertFailureAndMessage(TestException.class, "onError");

        assertEquals(0, counts[0]);
        assertEquals(1, counts[1]);
        assertEquals(0, counts[2]);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(MaybeSubject.<Integer>create()
                .as(Maybes.flatMapSingle(
                        new Function<Integer, Single<Integer>>() {
                            @Override
                            public Single<Integer> apply(Integer e)
                                    throws Exception {
                                return Single.just(2);
                            }
                        },
                        new Function<Throwable, Single<Integer>>() {
                            @Override
                            public Single<Integer> apply(Throwable e)
                                    throws Exception {
                                return Single.error(e);
                            }
                        },
                        new Callable<Single<Integer>>() {
                            @Override
                            public Single<Integer> call() throws Exception {
                                return Single.just(3);
                            }
                        }
                ))
        );
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybeToSingle(new Function<Maybe<Integer>, Single<Integer>>() {
            @Override
            public Single<Integer> apply(Maybe<Integer> c) throws Exception {
                return c.as(Maybes.flatMapSingle(
                        new Function<Integer, Single<Integer>>() {
                            @Override
                            public Single<Integer> apply(Integer e)
                                    throws Exception {
                                return Single.just(2);
                            }
                        },
                        new Function<Throwable, Single<Integer>>() {
                            @Override
                            public Single<Integer> apply(Throwable e)
                                    throws Exception {
                                return Single.error(e);
                            }
                        },
                        new Callable<Single<Integer>>() {
                            @Override
                            public Single<Integer> call() throws Exception {
                                return Single.just(3);
                            }
                        }
                ));
            }
        });
    }
}
