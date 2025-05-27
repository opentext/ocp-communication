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

package com.opentext.exstream.sdk.exstreamApi.model.dto;

import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ResourceType;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.WorkflowState;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for filters that can be applied to a request when listing DAS resources or getting links<br>
 * Properties that are null are excluded from the request<br>
 * Note: This does not contain all possible filters
 */
public class ResourceFilter {
    // Filter by resource type
    private List<ResourceType> types;

    // Filter by resource state
    private List<WorkflowState> states;

    // Only return the latest version of resources when true, gets all versions when false
    private Boolean latestVersion;

    private List<ResourceType> rFilterTypes;

    private List<WorkflowState> rFilterStates;

    //region Getters and Setters

    public List<ResourceType> getTypes() {
        return types;
    }

    public ResourceFilter setTypes(List<ResourceType> types) {
        this.types = types;
        return this;
    }

    public ResourceFilter addType(ResourceType type) {
        if (this.types == null) {
            this.types = new ArrayList<>(1);
        }
        this.types.add(type);
        return this;
    }

    public List<WorkflowState> getStates() {
        return states;
    }

    public ResourceFilter setStates(List<WorkflowState> states) {
        this.states = states;
        return this;
    }

    public ResourceFilter addState(WorkflowState state) {
        if (this.states == null) {
            this.states = new ArrayList<>(1);
        }
        this.states.add(state);
        return this;
    }

    public Boolean getLatestVersion() {
        return latestVersion;
    }

    public ResourceFilter setLatestVersion(Boolean latestVersion) {
        this.latestVersion = latestVersion;
        return this;
    }

    public List<ResourceType> getRfilterTypes() {
        return rFilterTypes;
    }

    public ResourceFilter setRfilterTypes(List<ResourceType> types) {
        this.rFilterTypes = types;
        return this;
    }

    public ResourceFilter addRfilterType(ResourceType type) {
        if (this.rFilterTypes == null) {
            this.rFilterTypes = new ArrayList<>(1);
        }
        this.rFilterTypes.add(type);
        return this;
    }

    public List<WorkflowState> getRfilterStates() {
        return rFilterStates;
    }

    public ResourceFilter setRfilterStates(List<WorkflowState> states) {
        this.rFilterStates = states;
        return this;
    }

    public ResourceFilter addRfilterState(WorkflowState state) {
        if (this.rFilterStates == null) {
            this.rFilterStates = new ArrayList<>(1);
        }
        this.rFilterStates.add(state);
        return this;
    }

    //endregion

    public MultiValueMap<String, String> getQueryParamMap() {
        MultiValueMap<String, String> queryParamMap = new LinkedMultiValueMap<>();

        if (latestVersion != null) {
            queryParamMap.add("filter.latestVersion", latestVersion.toString());
        }
        if (types != null) {
            String typeString = types.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            queryParamMap.add("filter.types", typeString);
        }
        if (states != null) {
            String stateString = states.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            queryParamMap.add("filter.states", stateString);
        }

        if (rFilterTypes != null) {
            String typeString = rFilterTypes.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            queryParamMap.add("rfilter.types", typeString);
        }
        if (rFilterStates != null) {
            String stateString = rFilterStates.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            queryParamMap.add("rfilter.states", stateString);
        }

        return queryParamMap;
    }
}
