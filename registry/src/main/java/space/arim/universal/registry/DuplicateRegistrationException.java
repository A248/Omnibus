/* 
 * UniversalRegistry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalRegistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalRegistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalRegistry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.registry;

/**
 * Thrown when it is attempted to register a {@link Registration} with the same provider
 * as an existing registration.
 * 
 * @author A248
 *
 */
public class DuplicateRegistrationException extends RuntimeException {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 6136966401403712313L;
	
	/**
	 * Creates the exception
	 * 
	 */
	public DuplicateRegistrationException() {
		
	}
	
	/**
	 * Creates the exception with a detail message
	 * 
	 * @param message the detail message
	 */
	public DuplicateRegistrationException(String message) {
		super(message);
	}
	
	/**
	 * Creates the exception with a cause
	 * 
	 * @param cause the case
	 */
	public DuplicateRegistrationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates the exception with a detail message and a cause
	 * 
	 * @param message the detail message
	 * @param cause the cause
	 */
	public DuplicateRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

}
