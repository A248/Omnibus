/* 
 * UniversalUtil
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalUtil is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalUtil. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.web;

/**
 * An exception caused by an unexpected (non 200) HTTP status code.
 * 
 * @author A248
 *
 */
public class HttpStatusException extends Exception {

	private static final long serialVersionUID = -744115099274403312L;
	
	/**
	 * The status code of the exception
	 * 
	 */
	public final HttpStatus status;
	
	/**
	 * Creates a HttpStatusException for the given code
	 * 
	 * @param status the HttpStatus
	 */
	public HttpStatusException(HttpStatus status) {
		super("Encountered HttpStatus " + status.getCode() + ": " + status.getName());
		this.status = status;
	}
	
	/**
	 * Creates a HttpStatusException for the given code
	 * 
	 * @param code the http status code
	 */
	public HttpStatusException(int code) {
		this(HttpStatus.fromCode(code));
	}

}
