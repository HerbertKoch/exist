/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2011 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.debugger.xquery;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.mina.core.session.IoSession;
import org.exist.EXistException;
import org.exist.debuggee.Debuggee;
import org.exist.debuggee.dbgp.packets.Command;
import org.exist.dom.QName;
import org.exist.memtree.DocumentImpl;
import org.exist.memtree.NodeImpl;
import org.exist.memtree.SAXAdapter;
import org.exist.storage.BrokerPool;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.BooleanValue;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class BreakpointList extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("breakpoint-list", Module.NAMESPACE_URI, Module.PREFIX), 
			"", 
			new SequenceType[] { 
				new FunctionParameterSequenceType(
					"session id", 
					Type.STRING, 
					Cardinality.EXACTLY_ONE, 
					""
				) 
			}, 
			new FunctionReturnSequenceType(
				Type.BOOLEAN, 
				Cardinality.EXACTLY_ONE, 
				""
			)
		)
	};

	public BreakpointList(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
			Debuggee dbgr = BrokerPool.getInstance().getDebuggee();
			
			IoSession session = (IoSession) dbgr.getSession(args[0].getStringValue());
			if (session == null) return BooleanValue.FALSE;

			Command command = new org.exist.debuggee.dbgp.packets.BreakpointList(session, "");
			command.exec();
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);	
			//TODO we should be able to cope with context.getBaseURI()				
			InputSource src = new InputSource(new ByteArrayInputStream(command.responseBytes()));
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			SAXAdapter adapter = new SAXAdapter();
	        reader.setContentHandler(adapter);
			reader.setContentHandler(adapter);
			reader.parse(src);
			
			DocumentImpl doc = (DocumentImpl) adapter.getDocument();
	        return (NodeImpl) doc.getFirstChild();

			
		} catch (Exception e) {
			throw new XPathException(this, e);
		}
	}
}