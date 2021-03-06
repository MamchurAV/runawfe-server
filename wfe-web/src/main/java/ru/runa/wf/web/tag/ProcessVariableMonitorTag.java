/*
 * This file is part of the RUNA WFE project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package ru.runa.wf.web.tag;

import java.util.List;
import java.util.Map;

import org.apache.ecs.html.A;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.tldgen.annotations.Attribute;
import org.tldgen.annotations.BodyContent;

import ru.runa.common.WebResources;
import ru.runa.common.web.Commons;
import ru.runa.common.web.html.HeaderBuilder;
import ru.runa.common.web.html.RowBuilder;
import ru.runa.common.web.html.StringsHeaderBuilder;
import ru.runa.common.web.html.TableBuilder;
import ru.runa.wf.web.MessagesProcesses;
import ru.runa.wf.web.html.ProcessVariablesRowBuilder;
import ru.runa.wfe.commons.SystemProperties;
import ru.runa.wfe.commons.web.PortletUrlType;
import ru.runa.wfe.execution.ProcessPermission;
import ru.runa.wfe.security.Permission;
import ru.runa.wfe.service.delegate.Delegates;
import ru.runa.wfe.var.dto.WfVariable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@org.tldgen.annotations.Tag(bodyContent = BodyContent.JSP, name = "processVariableMonitor")
public class ProcessVariableMonitorTag extends ProcessBaseFormTag {

    private static final long serialVersionUID = 161759402000861245L;

    private Long identifiableId;

    @Attribute(required = false, rtexprvalue = true)
    @Override
    public void setIdentifiableId(Long id) {
        identifiableId = id;
    }

    @Override
    public Long getIdentifiableId() {
        return identifiableId;
    }

    @Override
    protected boolean isFormButtonVisible() {
        return false;
    }

    @Override
    protected void fillFormData(TD tdFormElement) {
        if (SystemProperties.isUpdateProcessVariablesInAPIEnabled() && Delegates.getExecutorService().isAdministrator(getUser())) {
            Table table = new Table();
            tdFormElement.addElement(table);
            table.addAttribute("width", "100%");

            TR updateVariableTR = new TR();
            table.addElement(updateVariableTR);

            Map<String, Object> params = Maps.newHashMap();
            params.put("id", identifiableId);
            String updateVariableUrl = Commons.getActionUrl(WebResources.ACTION_UPDATE_PROCESS_VARIABLES, params, pageContext, PortletUrlType.Render);
            A a = new A(updateVariableUrl, MessagesProcesses.LINK_UPDATE_VARIABLE.message(pageContext));
            updateVariableTR.addElement(new TD(a).addAttribute("align", "right"));
        }
        List<WfVariable> variables = Delegates.getExecutionService().getVariables(getUser(), getIdentifiableId());
        List<String> headerNames = Lists.newArrayList();
        headerNames.add(MessagesProcesses.LABEL_VARIABLE_NAME.message(pageContext));
        headerNames.add(MessagesProcesses.LABEL_VARIABLE_TYPE.message(pageContext));
        if (WebResources.isDisplayVariablesJavaType()) {
            headerNames.add("Java " + MessagesProcesses.LABEL_VARIABLE_TYPE.message(pageContext));
        }
        headerNames.add(MessagesProcesses.LABEL_VARIABLE_VALUE.message(pageContext));
        HeaderBuilder headerBuilder = new StringsHeaderBuilder(headerNames);

        RowBuilder rowBuilder = new ProcessVariablesRowBuilder(getIdentifiableId(), variables, pageContext);
        tdFormElement.addElement(new TableBuilder().build(headerBuilder, rowBuilder));
    }

    @Override
    protected Permission getPermission() {
        return ProcessPermission.READ;
    }

    @Override
    protected String getTitle() {
        return MessagesProcesses.TITLE_INSANCE_VARIABLE_LIST.message(pageContext);
    }
}
