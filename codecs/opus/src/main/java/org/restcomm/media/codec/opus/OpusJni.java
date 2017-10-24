/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
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

package org.restcomm.media.codec.opus;

/**
 * Implements access to JNI layer for native Opus library.
 * 
 * @author Vladimir Morosev (vladimir.morosev@telestax.com)
 * 
 */
public class OpusJni {
    
   public static interface Observer {
      public void onHello();
   }
	   
   static {
      System.loadLibrary("opus");
      System.loadLibrary("opus_jni");
   }

   public native void sayHelloNative();
   public native void initEncoderNative(String encoderId);
   public native void initDecoderNative(String decoderId);
   public native void closeEncoderNative(String encoderId);
   public native void closeDecoderNative(String decoderId);
   public native byte[] encodeNative(String encoderId, short[] pcmData);
   public native short[] decodeNative(String decoderId, byte[] opusData);
   public native void setOpusObserverNative(Observer observer);
   public native void unsetOpusObserverNative();
}