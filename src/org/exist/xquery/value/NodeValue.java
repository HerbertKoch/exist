/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-03 Wolfgang M. Meier
 *  wolfgang@exist-db.org
 *  http://exist.sourceforge.net
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
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id$
 */
package org.exist.xquery.value;

import org.exist.xquery.XPathException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Represents a node value. May either be an in-memory node
 * or a persistent node.
 * 
 * @author Wolfgang Meier (wolfgang@exist-db.org)
 */
public interface NodeValue extends Item, Sequence {
	
	/** Node is a constructed in-memory node */
	public final static int IN_MEMORY_NODE = 0;
	
	/** Node is a persistent, i.e. stored in the database */
	public final static int PERSISTENT_NODE = 1;
	
	/**
	 * Returns true if this node has the same identity as another
	 * node. Used to implement "is" and "isnot" comparisons.
	 * 
	 * @param other
	 * @return
	 * @throws XPathException
	 */
	public boolean equals(NodeValue other) throws XPathException;
	
	/**
	 * Returns true if this node comes before another node in
	 * document order.
	 * 
	 * @param other
	 * @return
	 * @throws XPathException
	 */
	public boolean before(NodeValue other) throws XPathException;
	
	/**
	 * Returns true if this node comes after another node in
	 * document order.
	 * 
	 * @param other
	 * @return
	 * @throws XPathException
	 */
	public boolean after(NodeValue other) throws XPathException;
	
	/**
	 * Returns the implementation-type of this node, i.e. either
	 * {@link #IN_MEMORY_NODE} or {@link #PERSISTENT_NODE}.
	 * 
	 * @return
	 */
	public int getImplementationType();
	
    public void addContextNode(NodeValue node);
    
	public Node getNode();
	
	public Document getOwnerDocument();
}
