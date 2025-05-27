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

// Javascript code for the sample.html page
// The code in this file demonstrates how to integrate with the API methods registered by EditorAPI.js

// The Empower Editor Automation API accessor object.
var empowerInstance = null;

function onClick_save() {
    const contentFrame = document.getElementById("contentFrame");
    if (!empowerInstance) {
        // INTEGRATOR:  Use window.location.origin if the origin of the iframe src matches the origin of the Empower server.
        // You might need to change window.location.origin to the origin of your Empower server (for example, "http://localhost:8080").
        empowerInstance = EditorAPI.getInstance(contentFrame.contentWindow, empowerOriginUrl);
    }
    empowerInstance.document.hasChanged(hasChangedSaveCallback);
}

function onClick_publish() {
    const contentFrame = document.getElementById("contentFrame");
    if (!empowerInstance) {
        // INTEGRATOR:  Use window.location.origin if the origin of the iframe src matches the origin of the Empower server.
        // You might need to change window.location.origin to the origin of your Empower server (for example, "http://localhost:9090").
        empowerInstance = EditorAPI.getInstance(contentFrame.contentWindow, empowerOriginUrl);
    }
    empowerInstance.document.hasChanged(hasChangedPublishCallback);
}

function onClick_generate() {
    const frameContainer = document.getElementById("frameContainer");
    if (frameContainer) frameContainer.style.display = "none";
    displayStatusMessage("Generating Empower document...")
    document.getElementById("generateForm").submit();
}

// The iframe is not displayed by default. Once the page has loaded, this will hide the status message
// and show the frame.
function onLoad_contentFrame() {
    document.getElementById("contentFrameStatus").style.display = "none";
    document.getElementById("contentFrame").style.display = "block";
}

// Save the document if it's dirty or submit the publish form if not.
function hasChangedPublishCallback(returnValue) {
    console.log(returnValue);

    if (returnValue.success === true && returnValue.isDirty === true) {
        empowerInstance.document.save(savePublishCallback);
    } else if (returnValue.isDirty === false) {
        submitPublishForm();
    }
}

// Save the document if it's dirty.
function hasChangedSaveCallback(returnValue) {
    console.log(returnValue);

    if (returnValue.success === true && returnValue.isDirty === true)
        empowerInstance.document.save((returnValue) => {
            if (returnValue.success === true)
                console.log("Document saved successfully.");
            else
                console.log("Document save failed: " + returnValue.message);
        });
}

// If the document saved successfully submit the form.
// Form submission will trigger fulfillment on the document.
function savePublishCallback(returnValue) {
    if (returnValue.success === false) {
        console.log("Document save failed: " + returnValue.message);
    } else {
        submitPublishForm();
    }
}

// Show/hide elements on the page and submit the publishForm
function submitPublishForm() {
    document.getElementById("frameContainer").style.display = "none";
    displayStatusMessage("Fulfilling Empower document...");
    document.getElementById("publishForm").submit();
}

// Displays an alert message with loading status
function displayStatusMessage(message) {
    document.getElementById("messageContainer").style.display = "block";
    document.getElementById("loadingMessage").textContent = message;
}

//The following specifies the callback for the load and click events,
// as well as the polyfill for window.location.origin.
window.addEventListener("DOMContentLoaded", function (event) {
    //This is a polyfill for window.location.origin.
    if (typeof location.origin === 'undefined')
        window.location.origin = window.location.protocol + '//' + window.location.host;

    // Register the click event callbacks
    document.getElementById("publishEmpower")?.addEventListener("click", onClick_publish);
    document.getElementById("generateEmpowerDoc")?.addEventListener("click", onClick_generate);
    document.getElementById("saveEmpower")?.addEventListener("click", onClick_save);
});

