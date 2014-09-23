/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

Sahi.prototype.originalEx = Sahi.prototype.ex;

Sahi.prototype.ex = function (isStep) {
    //console.log("init sakuli func!");

    //determine if custom delay is active
    if (this.isSakuliDelayActive == undefined) {
        var sakuliDelayActive = this.getServerVar("sakuli-delay-active");
        if (sakuliDelayActive != undefined) {
            this.isSakuliDelayActive = sakuliDelayActive;
            console.log("set isSakuliDelayActive = " + this.isSakuliDelayActive);
        }
    }

    if (this.isSakuliDelayActive == true) {
        //check delay
        //console.log("custom sakuli delay");
        var delayTime = this.getServerVar("sakuli-delay-time");
        if (delayTime != undefined && delayTime > 0) {
            this.interval = delayTime;
            console.log("set sahi-request-delay to " + delayTime);
        } else {
            this.interval = this.INTERVAL;
        }
    }

    //console.log("execute orig ex");
    return this.originalEx(isStep);
};