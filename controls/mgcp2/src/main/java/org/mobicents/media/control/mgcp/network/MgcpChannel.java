/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.media.control.mgcp.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.mobicents.media.control.mgcp.message.MessageDirection;
import org.mobicents.media.control.mgcp.message.MgcpMessage;
import org.mobicents.media.control.mgcp.message.MgcpMessageObserver;
import org.mobicents.media.control.mgcp.message.MgcpMessageSubject;
import org.mobicents.media.server.io.network.channel.MultiplexedNetworkChannel;
import org.mobicents.media.server.io.network.channel.NetworkGuard;

/**
 * UDP channel that handles MGCP traffic.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class MgcpChannel extends MultiplexedNetworkChannel implements MgcpMessageSubject, MgcpMessageObserver {

    private static final Logger log = Logger.getLogger(MgcpChannel.class);

    // Packet Handlers
    private final MgcpPacketHandler mgcpHandler;

    // Message Observers
    private final Collection<MgcpMessageObserver> observers;

    public MgcpChannel(NetworkGuard networkGuard, MgcpPacketHandler packetHandler) {
        super(networkGuard, packetHandler);

        // Packet Handlers
        this.mgcpHandler = packetHandler;
        this.mgcpHandler.observe(this);

        // Observers
        this.observers = new CopyOnWriteArrayList<>();
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void observe(MgcpMessageObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void forget(MgcpMessageObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notify(Object originator, InetSocketAddress from, InetSocketAddress to, MgcpMessage message, MessageDirection direction) {
        Iterator<MgcpMessageObserver> iterator = this.observers.iterator();
        while (iterator.hasNext()) {
            MgcpMessageObserver observer = (MgcpMessageObserver) iterator.next();
            if (observer != originator) {
                observer.onMessage(from, to, message, direction);
            }
        }
    }

    @Override
    public void onMessage(InetSocketAddress from, InetSocketAddress to, MgcpMessage message, MessageDirection direction) {
        // Forward message to registered observers
        notify(this, from, to, message, direction);
    }

    public void send(InetSocketAddress to, MgcpMessage message) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Outgoing MGCP message to " + to.toString() + ":\n" + message.toString());
        }

        final byte[] data = message.toString().getBytes();
        send(data, to);
    }

}