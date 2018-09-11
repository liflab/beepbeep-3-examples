/*
    BeepBeep processor chains for the CRV 2016
    Copyright (C) 2016 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pingus;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.ltl.Always;
import ca.uqac.lif.cep.ltl.NewTrooleanForAll;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.ltl.TrooleanCast;
import ca.uqac.lif.cep.xml.XPathFunction;

public class TurnAround extends GroupProcessor
{
  ///
  public TurnAround() throws ConnectorException
  {
    super(1, 1);
    GroupProcessor next_g = new GroupProcessor(1, 1);
    {
      Fork fork = new Fork(2);
      next_g.addProcessor(fork);
      next_g.associateInput(0, fork, 0);
      ApplyFunction distance_x_now = new ApplyFunction(new DistanceX());
      next_g.addProcessor(distance_x_now);
      Connector.connect(fork, 0, distance_x_now, 0);
      Trim next = new Trim(3);
      next_g.addProcessor(next);
      Connector.connect(fork, 1, next, 0);
      ApplyFunction distance_x_later = new ApplyFunction(new DistanceX());
      next_g.addProcessor(distance_x_later);
      Connector.connect(next, distance_x_later);
      ApplyFunction gt = new ApplyFunction(Numbers.isLessThan);
      next_g.addProcessor(gt);
      Connector.connect(distance_x_now, 0, gt, 0);
      Connector.connect(distance_x_later, 0, gt, 1);
      ApplyFunction cast = new ApplyFunction(TrooleanCast.instance);
      next_g.addProcessor(cast);
      Connector.connect(gt, cast);
      next_g.associateOutput(0, cast, 0);
    }
    GroupProcessor imp_g = new GroupProcessor(1, 1);
    Fork fork = new Fork(2);
    imp_g.addProcessor(fork);
    ApplyFunction collides = new ApplyFunction(new Collides());
    imp_g.addProcessor(collides);
    Connector.connect(fork, 0, collides, 0);
    //imp_g.addProcessor(fa3);
    imp_g.addProcessor(next_g);
    Connector.connect(fork, 1, next_g, 0);
    ApplyFunction implies = new ApplyFunction(Troolean.IMPLIES_FUNCTION);
    imp_g.addProcessor(implies);
    Connector.connect(collides, 0, implies, 0);
    Connector.connect(next_g, 0, implies, 1);
    imp_g.associateInput(0, fork, 0);
    imp_g.associateOutput(0, implies, 0);
    NewTrooleanForAll fa2 = new NewTrooleanForAll("p2", new XPathFunction("message/characters/character[status=BLOCKER]/id/text()"), imp_g);
    NewTrooleanForAll fa1 = new NewTrooleanForAll("p1", new XPathFunction("message/characters/character[status=WALKER]/id/text()"), fa2);
    Always a = new Always(fa1);
    //Connector.connect(fa1, a);
    associateInput(0, a, 0);
    associateOutput(0, a, 0);
  }
  ///
}
