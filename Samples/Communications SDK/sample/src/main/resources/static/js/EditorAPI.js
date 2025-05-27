/*
 * Copyright 2023 Open Text Corporation, All Rights Reserved.
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// This is the Empower EditorAPI.js file included with Empower.
// This file should be included by pages that need to integrate with the Empower Javascript callback API.
// For more information on the Empower API, see the OpenText Exstream Empower Server and API Help documentation.

var EditorAPI;
(function (EditorAPI) {
    var call = /** @class */ (function () {
        function call(uniqueIdentifier, methodName, args) {
            this.uniqueIdentifier = uniqueIdentifier;
            this.methodName = methodName;
            this.args = args;
            this.action = "method call";
        }
        return call;
    }());
    var METHOD_RESPONSE_ACTION = "method response";
    var METHOD_SUBSCRIBED_ACTION = "method subscribed";
    function getInstance(targetWindow, targetOrigin) {
        return new empowerInstance(targetWindow, targetOrigin);
    }
    EditorAPI.getInstance = getInstance;
    var empowerInstance = /** @class */ (function () {
        function empowerInstance(windowToWhichMessagesWillBeSent, originToWhichMessagesWillBePosted) {
            this.windowToWhichMessagesWillBeSent = windowToWhichMessagesWillBeSent;
            this.originToWhichMessagesWillBePosted = originToWhichMessagesWillBePosted;
            this.callBacks = {};
            //TRACE.trace("built empowerInstance");
            this.document = new documentAPI(this);
            this.init();
        }
        //////////////
        // internal //
        //////////////
        empowerInstance.prototype.simpleCall = function (methodName, args, fnCallback) {
            if (!utils.isFunction)
                throw new Error(methodName + " expects a callback");
            var callId = utils.generatePseudoGuid();
            var callToSend = new call(callId, methodName, args);
            this.callBacks[callId] = fnCallback;
            this.windowToWhichMessagesWillBeSent.postMessage(JSON.stringify(callToSend), this.originToWhichMessagesWillBePosted);
        };
        //////////////
        // internal //
        //////////////
        empowerInstance.prototype.init = function () {
            var _this = this;
            window.addEventListener("message", function (e) { return messageHandling.handleMessage(e, _this.callBacks); }, false);
        };
        return empowerInstance;
    }());
    var documentAPI = /** @class */ (function () {
        function documentAPI(parent) {
            this.parent = parent;
        }
        // These are the methods, identitified by a GUID, that will be called when an API call returns.
        documentAPI.prototype.hasChanged = function (fnCallback, includeUncommitedChangesInActiveVarArea) {
            var args = includeUncommitedChangesInActiveVarArea !== undefined ? [includeUncommitedChangesInActiveVarArea] : undefined;
            this.parent.simpleCall("EditorAPI.document.hasChanged", args, fnCallback);
        };
        documentAPI.prototype.save = function (fnCallback, includeUncommitedChangesInActiveVarArea) {
            var args = includeUncommitedChangesInActiveVarArea !== undefined ? [includeUncommitedChangesInActiveVarArea] : undefined;
            this.parent.simpleCall("EditorAPI.document.save", args, fnCallback);
        };
        documentAPI.prototype.implicitSave = function (fnCallback, includeUncommitedChangesInActiveVarArea) {
            var args = includeUncommitedChangesInActiveVarArea !== undefined ? [includeUncommitedChangesInActiveVarArea] : undefined;
            this.parent.simpleCall("EditorAPI.document.implicitSave", args, fnCallback);
        };
        documentAPI.prototype.getFirstRequiredEditArea = function (fnCallback) {
            this.parent.simpleCall("EditorAPI.document.getFirstRequiredEditArea", [], fnCallback);
        };
        documentAPI.prototype.navigateToFirstRequiredEditableArea = function (fnCallback, destination) {
            var args = [destination];
            this.parent.simpleCall("EditorAPI.document.navigateToFirstRequiredEditableArea", args, fnCallback);
        };
        documentAPI.prototype.registerTabNavCallback = function (fnCallback) {
            console.log("%cReached client EditorAPI::registerTabNavCallback\n", "color:green");
            this.parent.simpleCall("EditorAPI.document.registerTabNavCallback", [], fnCallback);
        };
        documentAPI.prototype.passTabNavControl = function (fnCallback, fromTop) {
            if (fromTop === void 0) { fromTop = true; }
            this.parent.simpleCall("EditorAPI.document.passTabNavControl", [fromTop], fnCallback);
        };
        documentAPI.prototype.addReplayEventListener = function (fnCallback, eventName) {
            var args = [eventName];
            this.parent.simpleCall("EditorAPI.document.addReplayEventListener", args, fnCallback);
        };
        return documentAPI;
    }());
    var utils;
    (function (utils) {
        // pure
        function isFunction(functionToCheck) {
            var getType = {};
            return functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
        }
        utils.isFunction = isFunction;
        // pure: Generates a unique identifier.  This ID is not guaranteed to be securely random or globally unique.
        function generatePseudoGuid() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.round(Math.random() * 16);
                var v = (c === 'x' ? r : (r & 0x3 | 0x8));
                return v.toString(16);
            });
        }
        utils.generatePseudoGuid = generatePseudoGuid;
    })(utils || (utils = {}));
    var messageHandling;
    (function (messageHandling) {
        function handleMessage(messageEvent, callBacks) {
            var response = messageHandling.parseMessage(messageEvent);
            validateMethodResponse(response, callBacks);
            //execute the callback associated with the callId GUID
            callBacks[response.uniqueIdentifier](response.returnValue);
            if (response.action !== METHOD_SUBSCRIBED_ACTION)
                delete callBacks[response.uniqueIdentifier];
            //TRACE.trace("EditorAPI received \'" + evt.data + "\' from " + evt.origin);
        }
        messageHandling.handleMessage = handleMessage;
        // pure
        function parseMessage(messageEvent) {
            try {
                return JSON.parse(messageEvent.data);
            }
            catch (_) {
                throw new Error("EditorAPI is unable to parse a message received from " + messageEvent.origin + " as JSON. The data was \'" +
                    messageEvent.data.toString() + "'");
            }
        }
        messageHandling.parseMessage = parseMessage;
        // pure
        function validateMethodResponse(response, callBacks) {
            var fnCallback = callBacks[response.uniqueIdentifier];
            if (fnCallback) {
                if (response.action !== METHOD_RESPONSE_ACTION && response.action !== METHOD_SUBSCRIBED_ACTION)
                    throw new Error("EditorAPI recieved an uUnexpected action \'" + response.action + "\' in response.");
            }
            else
                throw ("EditorAPI recieved a response with an unknown uniqueIdentifier");
        }
        messageHandling.validateMethodResponse = validateMethodResponse;
    })(messageHandling || (messageHandling = {}));
})(EditorAPI || (EditorAPI = {}));
