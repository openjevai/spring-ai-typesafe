/*
 * Copyright 2026 - 2026 the original author or authors.
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

package org.springaicommunity.typesafe.autoconfigure;



import java.time.Duration;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springaicommunity.typesafe.OpenJEVConstants;
import org.springaicommunity.typesafe.RetryPolicy;
import org.springaicommunity.typesafe.TypeSafeConstants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Configuration for the Jev System One client.
 *
 * <pre>{@code
 * spring.ai.typesafe.api-key=${TYPESAFE_API_KEY}
 * spring.ai.typesafe.model=jev-latest
 * spring.ai.typesafe.timeout=10s
 * spring.ai.typesafe.retry.max-retries=2
 * }</pre>
 *
 * @author Christian Tzolov
 */
@ConfigurationProperties(TypeSafeProperties.CONFIG_PREFIX)
public class TypeSafeProperties {

	public static final String CONFIG_PREFIX = "spring.ai.typesafe";

	/**
	 * TypeSafe API key. Without it no client is created.
	 */
	private @Nullable String apiKey;

	/**
	 * OpenJEV API key — a free community gateway to the same Jev model. When set and
	 * no TypeSafe key is configured, the client uses OpenJEV automatically.
	 */
	private @Nullable String openjevApiKey;

	/**
	 * Explicit provider selection: {@code openjev} or {@code typesafe}. When unset the
	 * provider is auto-detected — TypeSafe wins if its key is set, otherwise OpenJEV.
	 */
	private @Nullable String provider;

	/**
	 * API root.
	 */
	private String baseUrl = TypeSafeConstants.DEFAULT_BASE_URL;

	/**
	 * Model name or alias applied to requests that do not name one.
	 */
	private String model = TypeSafeConstants.DEFAULT_MODEL;

	/**
	 * Timeout of each HTTP operation.
	 */
	private Duration timeout = TypeSafeConstants.DEFAULT_TIMEOUT;

	private final Retry retry = new Retry();

	public @Nullable String getApiKey() {
		return this.apiKey;
	}

	public void setApiKey(@Nullable String apiKey) {
		this.apiKey = apiKey;
	}

	public @Nullable String getOpenjevApiKey() {
		return this.openjevApiKey;
	}

	public void setOpenjevApiKey(@Nullable String openjevApiKey) {
		this.openjevApiKey = openjevApiKey;
	}

	public @Nullable String getProvider() {
		return this.provider;
	}

	public void setProvider(@Nullable String provider) {
		this.provider = provider;
	}

	/**
	 * Resolves the provider selection: an explicit {@code provider} wins; otherwise
	 * TypeSafe wins if its key is set, otherwise OpenJEV if its key is set.
	 * @return {@code true} when OpenJEV should be used
	 */
	public boolean resolveUseOpenJEV() {
		if (OpenJEVConstants.PROVIDER_OPENJEV.equalsIgnoreCase(this.provider)) {
			return true;
		}
		if (OpenJEVConstants.PROVIDER_TYPESAFE.equalsIgnoreCase(this.provider)) {
			return false;
		}
		return !StringUtils.hasText(this.apiKey) && StringUtils.hasText(this.openjevApiKey);
	}

	/**
	 * Resolves the effective API key based on the provider selection.
	 * @return the key to use, or {@code null} when none is configured
	 */
	public @Nullable String resolveApiKey() {
		return resolveUseOpenJEV() ? this.openjevApiKey : this.apiKey;
	}

	/**
	 * Resolves the effective base URL, overriding the TypeSafe default with the OpenJEV
	 * default when OpenJEV is selected and the user has not changed it from the default.
	 * @return the base URL to use
	 */
	public String resolveBaseUrl() {
		if (resolveUseOpenJEV() && TypeSafeConstants.DEFAULT_BASE_URL.equals(this.baseUrl)) {
			return OpenJEVConstants.DEFAULT_BASE_URL;
		}
		return this.baseUrl;
	}

	/**
	 * Resolves the effective model, overriding the TypeSafe default with the OpenJEV
	 * default when OpenJEV is selected and the user has not changed it from the default.
	 * @return the model to use
	 */
	public String resolveModel() {
		if (resolveUseOpenJEV() && TypeSafeConstants.DEFAULT_MODEL.equals(this.model)) {
			return OpenJEVConstants.DEFAULT_MODEL;
		}
		return this.model;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Duration getTimeout() {
		return this.timeout;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

	public Retry getRetry() {
		return this.retry;
	}

	/**
	 * Assembles the configured {@link RetryPolicy}.
	 * @return the policy described by these properties
	 */
	public RetryPolicy toRetryPolicy() {
		return RetryPolicy.builder()
			.maxRetries(this.retry.getMaxRetries())
			.initialBackoff(this.retry.getInitialBackoff())
			.maxBackoff(this.retry.getMaxBackoff())
			.jitter(this.retry.getJitter())
			.retryableStatuses(this.retry.getStatuses())
			.respectRetryAfter(this.retry.isRespectRetryAfter())
			.retryConnectionErrors(this.retry.isRetryConnectionErrors())
			.totalTimeout(this.retry.getTotalTimeout())
			.build();
	}

	/**
	 * How transient failures are retried.
	 */
	public static class Retry {

		/**
		 * Retries after the initial attempt. Zero disables retrying.
		 */
		private int maxRetries = 2;

		/**
		 * First backoff delay, doubled on each subsequent attempt.
		 */
		private Duration initialBackoff = Duration.ofMillis(500);

		/**
		 * Upper bound on a single backoff delay.
		 */
		private Duration maxBackoff = Duration.ofSeconds(5);

		/**
		 * Fraction of each delay randomly subtracted, between 0 and 1.
		 */
		private double jitter = 0.25d;

		/**
		 * Status codes retried on top of every 5xx, which is always retried.
		 */
		private Set<Integer> statuses = RetryPolicy.DEFAULT_RETRYABLE_STATUSES;

		/**
		 * Whether a server stated wait overrides the computed backoff.
		 */
		private boolean respectRetryAfter = true;

		/**
		 * Whether failures without an HTTP response are retried.
		 */
		private boolean retryConnectionErrors = true;

		/**
		 * Budget for a whole call including waits.
		 */
		private @Nullable Duration totalTimeout = Duration.ofSeconds(30);

		public int getMaxRetries() {
			return this.maxRetries;
		}

		public void setMaxRetries(int maxRetries) {
			this.maxRetries = maxRetries;
		}

		public Duration getInitialBackoff() {
			return this.initialBackoff;
		}

		public void setInitialBackoff(Duration initialBackoff) {
			this.initialBackoff = initialBackoff;
		}

		public Duration getMaxBackoff() {
			return this.maxBackoff;
		}

		public void setMaxBackoff(Duration maxBackoff) {
			this.maxBackoff = maxBackoff;
		}

		public double getJitter() {
			return this.jitter;
		}

		public void setJitter(double jitter) {
			this.jitter = jitter;
		}

		public Set<Integer> getStatuses() {
			return this.statuses;
		}

		public void setStatuses(Set<Integer> statuses) {
			this.statuses = statuses;
		}

		public boolean isRespectRetryAfter() {
			return this.respectRetryAfter;
		}

		public void setRespectRetryAfter(boolean respectRetryAfter) {
			this.respectRetryAfter = respectRetryAfter;
		}

		public boolean isRetryConnectionErrors() {
			return this.retryConnectionErrors;
		}

		public void setRetryConnectionErrors(boolean retryConnectionErrors) {
			this.retryConnectionErrors = retryConnectionErrors;
		}

		public @Nullable Duration getTotalTimeout() {
			return this.totalTimeout;
		}

		public void setTotalTimeout(@Nullable Duration totalTimeout) {
			this.totalTimeout = totalTimeout;
		}

	}

}
