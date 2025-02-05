/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
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
package org.jkiss.dbeaver.model.text.parser;


/**
 * A token to be returned by a rule.
 */
public interface TPToken {

	/**
	 * Return whether this token is undefined.
	 *
	 * @return <code>true</code>if this token is undefined
	 */
	boolean isUndefined();

	/**
	 * Return whether this token represents a whitespace.
	 *
	 * @return <code>true</code>if this token represents a whitespace
	 */
	boolean isWhitespace();

	/**
	 * Return whether this token represents End Of File.
	 *
	 * @return <code>true</code>if this token represents EOF
	 */
	boolean isEOF();

	/**
	 * Return whether this token is neither undefined, nor whitespace, nor EOF.
	 *
	 * @return <code>true</code>if this token is not undefined, not a whitespace, and not EOF
	 */
	boolean isOther();

	/**
	 * Return a data attached to this token. The semantics of this data kept undefined by this interface.
	 *
	 * @return the data attached to this token.
	 */
	Object getData();

}
