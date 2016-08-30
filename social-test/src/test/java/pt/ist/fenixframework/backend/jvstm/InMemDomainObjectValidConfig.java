/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of social-test.
 *
 * social-test is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * social-test is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with social-test.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixframework.backend.jvstm;

import pt.ist.fenixframework.FenixFramework;

/***
 * 
 * Used to initialize {@link FenixFramework} with {@link InMemDomainObjectValidBackEnd}
 * 
 * Used in fenix-framework.properties for testing.
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 *
 */
public class InMemDomainObjectValidConfig extends JVSTMConfig {

    public InMemDomainObjectValidConfig() {
        this.backEnd = new InMemDomainObjectValidBackEnd();
    }
}