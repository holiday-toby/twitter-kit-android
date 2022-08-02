/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetcomposer;

import static org.mockito.Mockito.mock;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCoreTestUtils;
import com.twitter.sdk.android.core.TwitterTestUtils;

import java.util.concurrent.ThreadPoolExecutor;

public class TweetComposerTest extends AndroidTestCase {
    private static final String TWITTER_NOT_INIT_ERROR_MSG =
            "Must initialize Twitter before using getInstance()";
    private TweetComposer tweetComposer;

    public void setUp() throws Exception {
        super.setUp();
        Twitter.initialize(new TwitterConfig.Builder(getContext())
                .executorService(mock(ThreadPoolExecutor.class))
                .build());
        tweetComposer = new TweetComposer();
        tweetComposer.instance = tweetComposer;
    }

    public void tearDown()  throws Exception {
        TwitterTestUtils.resetTwitter();
        TwitterCoreTestUtils.resetTwitterCore();
        tweetComposer.instance = null;

        super.tearDown();
    }

    public void testBuilder_image() {
        final Context context = getContext();
        final Uri uri = Uri.parse("https://upload-images.jianshu.io/upload_images/3852552-51ef93d38f9f1596.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp");
        final TweetComposer.Builder builder = new TweetComposer.Builder(context).image(uri);
        final Intent intent = builder.createTwitterIntent();
        assertEquals(uri, intent.getParcelableExtra(Intent.EXTRA_STREAM));
    }

    public void testGetVersion() {
        final String version =   BuildConfig.BUILD_NUMBER;
        assertEquals(version, tweetComposer.getVersion());
    }

    public void testGetIdentifier() {
        final String identifier = BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID;
        assertEquals(identifier, tweetComposer.getIdentifier());
    }

    public void testGetInstance_twitterNotInitialized() throws Exception {
        try {
            TwitterTestUtils.resetTwitter();
            TwitterCoreTestUtils.resetTwitterCore();
            tweetComposer.instance = null;

            TweetComposer.getInstance();
            fail("Should fail if Twitter is not initialized");
        } catch (IllegalStateException e) {
            assertEquals(TWITTER_NOT_INIT_ERROR_MSG, e.getMessage());
        }
    }
}
