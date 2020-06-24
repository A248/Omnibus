/* 
 * Universal-events
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-events is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-events is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-events. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

/**
 * Thrown by some {@link Events} implementations to indicate an illegal listener object. <br>
 * In other implementations, other unchecked exceptions may be thrown.
 * 
 * @author A248
 *
 */
public class IllegalListenerException extends IllegalArgumentException {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 3055404757041865882L;
	
	/**
	 * Creates the exception
	 * 
	 */
    public IllegalListenerException() {
        
    }

    /**
     * Creates the exception with the specified message
     * 
     * @param message the message
     */
    public IllegalListenerException(String message) {
        super(message);
    }

    /**
     * Creates the exception with the specified message and cause
     * 
     * @param message the message
     * @param cause the cause
     */
    public IllegalListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates the exception with the specified cause
     * 
     * @param cause the cause
     */
    public IllegalListenerException(Throwable cause) {
        super(cause);
    }

}
