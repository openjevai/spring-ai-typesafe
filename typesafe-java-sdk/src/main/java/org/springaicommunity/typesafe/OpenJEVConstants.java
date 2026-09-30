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

package org.springaicommunity.typesafe;

/**
 * Environment variable names and client defaults for the OpenJEV gateway — a free
 * community gateway to the same Jev model that {@link TypeSafeConstants} addresses
 * directly. OpenJEV never replaces TypeSafe; these constants are consulted only when
 * the provider selection picks OpenJEV (explicit {@code JEV_PROVIDER=openjev}, or
 * auto-detection when only {@code OPENJEV_API_KEY} is set).
 *
 * <p>Same request/response contract as TypeSafe: the only differences are the endpoint
 * host, the model id, the key environment variable, and that OpenJEV signals overload
 * with HTTP 503 instead of the non-standard 529.
 *
 * @author OpenJEV
 */
public final class OpenJEVConstants {

	/** Environment variable holding the OpenJEV API key. */
	public static final String API_KEY_ENV = "OPENJEV_API_KEY";

	/** Environment variable overriding the OpenJEV API root. */
	public static final String BASE_URL_ENV = "OPENJEV_BASE_URL";

	/** Environment variable overriding the default model when using OpenJEV. */
	public static final String DEFAULT_MODEL_ENV = "OPENJEV_DEFAULT_MODEL";

	/** The standard OpenJEV API root. */
	public static final String DEFAULT_BASE_URL = "https://api.openjev.sh";

	/** The model used when none is configured and OpenJEV is the selected provider. */
	public static final String DEFAULT_MODEL = "openjev";

	/** Environment variable for explicit provider selection. Values: {@code openjev}, {@code typesafe}. */
	public static final String PROVIDER_ENV = "JEV_PROVIDER";

	/** Provider value selecting OpenJEV. */
	public static final String PROVIDER_OPENJEV = "openjev";

	/** Provider value selecting TypeSafe (explicit form of the default). */
	public static final String PROVIDER_TYPESAFE = "typesafe";

	private OpenJEVConstants() {
	}

}
