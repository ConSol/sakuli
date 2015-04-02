/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2015 the original author or authors.
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

/**** Exclude this global variables from JSLint Warnings ****/
/* Packages*/

/**
 * MouseButton - representing the possible mouse action button.
 *
 * The following __MouseButton__ values are possible:
 *
 * `LEFT`, `RIGHT`, `MIDDLE`
 *
 * @example Press and release the right mouse button vor 3 seconds on a specified region:
 * ```
 * var region = new Region().find("your-pattern.png");
 * region.mouseDown(MouseButton.RIGHT).sleep(3).mouseUp(MouseButton.RIGHT);
 * ```
 * @namespace MouseButton
 */
MouseButton = {
    LEFT: Packages.org.sakuli.actions.screenbased.MouseButton.LEFT,
    RIGHT: Packages.org.sakuli.actions.screenbased.MouseButton.RIGHT,
    MIDDLE: Packages.org.sakuli.actions.screenbased.MouseButton.MIDDLE
};




